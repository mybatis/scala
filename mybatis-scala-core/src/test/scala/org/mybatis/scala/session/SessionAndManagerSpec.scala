/*
 *    Copyright 2011-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.scala.session

import org.apache.ibatis.executor.BatchResult
import org.apache.ibatis.session.{ResultContext, SqlSession, SqlSessionFactory}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.lang.reflect.{InvocationHandler, Method, Proxy}
import java.util
import scala.collection.mutable

class SessionAndManagerSpec extends AnyFunSpec with Matchers {

  private case class SqlSessionState(
    calls: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty,
    var commitCount: Int = 0,
    var rollbackCount: Int = 0,
    var closeCount: Int = 0,
    var clearCacheCount: Int = 0,
    var rollbackThrows: Boolean = false
  )

  private case class SqlSessionFactoryState(
    openSessionSignatures: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty
  )

  private def defaultValue(returnType: Class[?]): AnyRef = {
    if (returnType == java.lang.Boolean.TYPE) java.lang.Boolean.FALSE
    else if (returnType == java.lang.Integer.TYPE) Int.box(0)
    else if (returnType == java.lang.Long.TYPE) Long.box(0L)
    else if (returnType == java.lang.Short.TYPE) Short.box(0)
    else if (returnType == java.lang.Byte.TYPE) Byte.box(0)
    else if (returnType == java.lang.Float.TYPE) Float.box(0f)
    else if (returnType == java.lang.Double.TYPE) Double.box(0d)
    else if (returnType == java.lang.Character.TYPE) Char.box('\u0000')
    else null
  }

  private def newSqlSessionProxy(state: SqlSessionState): SqlSession = {
    val handler = new InvocationHandler {
      override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef = {
        state.calls += method.getName
        method.getName match {
          case "selectOne" =>
            if (Option(args).exists(_.length == 1)) "one"
            else "one-with-param"
          case "selectList" =>
            new util.ArrayList[AnyRef](util.Arrays.asList("a", "b"))
          case "selectMap" =>
            val m = new util.HashMap[String, String]()
            m.put("k", "v")
            m
          case "insert" => Int.box(11)
          case "update" => Int.box(12)
          case "delete" => Int.box(13)
          case "commit" =>
            state.commitCount += 1
            null
          case "rollback" =>
            state.rollbackCount += 1
            if (state.rollbackThrows) throw new RuntimeException("rollback-failure")
            null
          case "clearCache" =>
            state.clearCacheCount += 1
            null
          case "close" =>
            state.closeCount += 1
            null
          case "flushStatements" =>
            new util.ArrayList[BatchResult]()
          case _ =>
            defaultValue(method.getReturnType)
        }
      }
    }
    Proxy.newProxyInstance(getClass.getClassLoader, Array(classOf[SqlSession]), handler).asInstanceOf[SqlSession]
  }

  private def newSqlSessionFactoryProxy(state: SqlSessionFactoryState, session: SqlSession): SqlSessionFactory = {
    val handler = new InvocationHandler {
      override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef = {
        method.getName match {
          case "openSession" =>
            val signature = s"openSession/${method.getParameterCount}"
            state.openSessionSignatures += signature
            session
          case _ =>
            defaultValue(method.getReturnType)
        }
      }
    }
    Proxy.newProxyInstance(getClass.getClassLoader, Array(classOf[SqlSessionFactory]), handler).asInstanceOf[SqlSessionFactory]
  }

  describe("Session wrapper") {
    it("should delegate all core operations to underlying SqlSession") {
      val state = SqlSessionState()
      val sqlSession = newSqlSessionProxy(state)
      val session = new Session(sqlSession)

      session.selectOne[String]("id") shouldBe "one"
      session.selectOne[String, String]("id", "p") shouldBe "one-with-param"
      session.selectList[String]("id") should contain inOrderOnly ("a", "b")
      session.selectList[String, String]("id", "p") should contain inOrderOnly ("a", "b")
      session.selectList[String, String]("id", "p", RowBounds(0, 10)) should contain inOrderOnly ("a", "b")
      session.selectMap[String, String]("id", "k") should contain ("k" -> "v")
      session.selectMap[String, String, String]("id", "p", "k") should contain ("k" -> "v")
      session.selectMap[String, String, String]("id", "p", "k", RowBounds(0, 10)) should contain ("k" -> "v")

      val handler: ResultHandler[String] = (_: ResultContext[? <: String]) => ()
      noException shouldBe thrownBy(session.select("id", "p", handler))
      noException shouldBe thrownBy(session.select("id", handler))
      noException shouldBe thrownBy(session.select("id", "p", RowBounds(0, 10), handler))

      session.insert("id") shouldBe 11
      session.insert("id", "p") shouldBe 11
      session.update("id") shouldBe 12
      session.update("id", "p") shouldBe 12
      session.delete("id") shouldBe 13
      session.delete("id", "p") shouldBe 13

      session.commit()
      session.commit(force = true)
      session.rollback()
      session.rollback(force = true)
      session.clearCache()
      session.flushStatements() shouldBe empty

      state.commitCount shouldBe 2
      state.rollbackCount shouldBe 2
      state.clearCacheCount shouldBe 1
    }
  }

  describe("SessionManager") {
    it("should execute readOnly and always rollback and close") {
      val sqlState = SqlSessionState()
      val factoryState = SqlSessionFactoryState()
      val session = newSqlSessionProxy(sqlState)
      val manager = new SessionManager(newSqlSessionFactoryProxy(factoryState, session))

      var closedByHook = 0
      manager.closeSessionHook(_ => closedByHook += 1)
      val result = manager.readOnly(ExecutorType.REUSE, TransactionIsolationLevel.READ_COMMITTED)(_ => "ok")

      result shouldBe "ok"
      sqlState.rollbackCount shouldBe 1
      closedByHook shouldBe 1
      factoryState.openSessionSignatures should contain("openSession/2")
    }

    it("should execute transaction and commit on success for overloads") {
      val sqlState = SqlSessionState()
      val factoryState = SqlSessionFactoryState()
      val session = newSqlSessionProxy(sqlState)
      val manager = new SessionManager(newSqlSessionFactoryProxy(factoryState, session))

      manager.transaction(ExecutorType.BATCH, TransactionIsolationLevel.SERIALIZABLE)(_ => ())
      manager.transaction(ExecutorType.REUSE)(_ => ())
      manager.transaction(TransactionIsolationLevel.READ_UNCOMMITTED)(_ => ())
      manager.transaction(ExecutorType.SIMPLE, autoCommit = false)(_ => ())
      manager.transaction(autoCommit = true)(_ => ())
      manager.transaction(_ => ())

      sqlState.commitCount shouldBe 6
      sqlState.rollbackCount shouldBe 0
      sqlState.closeCount shouldBe 6
      factoryState.openSessionSignatures should contain allOf ("openSession/2", "openSession/1")
    }

    it("should rollback and rethrow when callback fails") {
      val sqlState = SqlSessionState()
      val session = newSqlSessionProxy(sqlState)
      val manager = new SessionManager(newSqlSessionFactoryProxy(SqlSessionFactoryState(), session))

      val err = the[IllegalStateException] thrownBy manager.transaction(_ => throw new IllegalStateException("boom"))

      err.getMessage shouldBe "boom"
      sqlState.commitCount shouldBe 0
      sqlState.rollbackCount shouldBe 1
      sqlState.closeCount shouldBe 1
    }

    it("should preserve original exception when rollback also fails") {
      val sqlState = SqlSessionState(rollbackThrows = true)
      val session = newSqlSessionProxy(sqlState)
      val manager = new SessionManager(newSqlSessionFactoryProxy(SqlSessionFactoryState(), session))

      val err = the[IllegalArgumentException] thrownBy manager.transaction(_ => throw new IllegalArgumentException("boom-2"))

      err.getMessage shouldBe "boom-2"
      sqlState.rollbackCount shouldBe 1
      sqlState.closeCount shouldBe 1
    }

    it("should execute managed sessions without commit/rollback") {
      val sqlState = SqlSessionState()
      val session = newSqlSessionProxy(sqlState)
      val manager = new SessionManager(newSqlSessionFactoryProxy(SqlSessionFactoryState(), session))

      manager.managed(ExecutorType.SIMPLE)(_ => ())
      manager.managed(_ => ())

      sqlState.commitCount shouldBe 0
      sqlState.rollbackCount shouldBe 0
      sqlState.closeCount shouldBe 2
    }
  }
}
