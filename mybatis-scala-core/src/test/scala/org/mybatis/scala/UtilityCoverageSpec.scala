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
package org.mybatis.scala

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.mybatis.scala.config.{ConfigurationException, DefaultScriptingDriver, Environment}
import org.mybatis.scala.session.{ExecutorType, ResultHandlerDelegator, TransactionIsolationLevel}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UtilityCoverageSpec extends AnyFunSpec with Matchers {

  describe("cache package constants") {
    it("should expose expected cache and eviction classes") {
      cache.DefaultCache.raw shouldBe classOf[org.apache.ibatis.cache.impl.PerpetualCache]
      cache.Eviction.LRU.raw shouldBe classOf[org.apache.ibatis.cache.decorators.LruCache]
      cache.Eviction.FIFO.raw shouldBe classOf[org.apache.ibatis.cache.decorators.FifoCache]
      cache.Eviction.SOFT.raw shouldBe classOf[org.apache.ibatis.cache.decorators.SoftCache]
      cache.Eviction.WEAK.raw shouldBe classOf[org.apache.ibatis.cache.decorators.WeakCache]
    }
  }

  describe("session wrapper enums") {
    it("should map executor and isolation wrappers to mybatis constants") {
      ExecutorType.SIMPLE.unwrap shouldBe org.apache.ibatis.session.ExecutorType.SIMPLE
      ExecutorType.REUSE.unwrap shouldBe org.apache.ibatis.session.ExecutorType.REUSE
      ExecutorType.BATCH.unwrap shouldBe org.apache.ibatis.session.ExecutorType.BATCH

      TransactionIsolationLevel.NONE.unwrap shouldBe org.apache.ibatis.session.TransactionIsolationLevel.NONE
      TransactionIsolationLevel.READ_COMMITTED.unwrap shouldBe org.apache.ibatis.session.TransactionIsolationLevel.READ_COMMITTED
      TransactionIsolationLevel.READ_UNCOMMITTED.unwrap shouldBe org.apache.ibatis.session.TransactionIsolationLevel.READ_UNCOMMITTED
      TransactionIsolationLevel.REPEATABLE_READ.unwrap shouldBe org.apache.ibatis.session.TransactionIsolationLevel.REPEATABLE_READ
      TransactionIsolationLevel.SERIALIZABLE.unwrap shouldBe org.apache.ibatis.session.TransactionIsolationLevel.SERIALIZABLE
      TransactionIsolationLevel.UNDEFINED.unwrap shouldBe null
    }
  }

  describe("result handler delegator") {
    it("should invoke callback with incoming context") {
      var seen: String = null
      val delegator = new ResultHandlerDelegator[String](ctx => seen = ctx.getResultObject)
      val ctx = new org.apache.ibatis.session.ResultContext[String] {
        override def getResultObject: String = "value"
        override def getResultCount: Int = 1
        override def isStopped: Boolean = false
        override def stop(): Unit = ()
      }

      delegator.handleResult(ctx)
      seen shouldBe "value"
    }
  }

  describe("simple config wrappers") {
    it("should expose environment unwrap fields") {
      val env = Environment("id", new JdbcTransactionFactory(), new UnpooledDataSource())

      env.unwrap.getId shouldBe "id"
      env.unwrap.getTransactionFactory shouldBe env.tf
      env.unwrap.getDataSource shouldBe env.ds
    }

    it("should expose default scripting driver singleton") {
      DefaultScriptingDriver shouldBe a[XMLLanguageDriver]
    }

    it("should preserve message and cause on ConfigurationException") {
      val cause = new RuntimeException("root")
      val ex = new ConfigurationException("msg", cause)

      ex.getMessage shouldBe "msg"
      ex.getCause shouldBe cause
    }

  }
}
