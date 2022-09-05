/*
 *    Copyright 2011-2015 the original author or authors.
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

import org.apache.ibatis.session.SqlSession
import scala.jdk.CollectionConverters._
import scala.collection.mutable._

/** SqlSession Wrapper.
  * You rarely use this class in an explicit manner.
  * == Usage ==
  * Used implicitly by mapped statements:
  * {{{
  * dbcontext.transaction( implicit session =>
  *   MyDAO.findAll()
  * )
  * }}}
  * @version \$Revision$
  */
class Session(sqls : SqlSession) {

  def selectOne[Result](statement : String) : Result = {
    sqls.selectOne(statement).asInstanceOf[Result]
  }

  def selectOne[Param,Result](statement : String, parameter : Param) : Result = {
    sqls.selectOne(statement, parameter).asInstanceOf[Result]
  }

  def selectList[Result](statement : String) : Seq[Result] = {
    sqls.selectList(statement).asScala.toBuffer
  }

  def selectList[Param,Result](statement : String, parameter : Param) : Seq[Result] = {
    sqls.selectList(statement, parameter).asScala.toBuffer
  }

  def selectList[Param,Result](statement : String, parameter : Param, rowBounds : RowBounds) : Seq[Result] = {
    sqls.selectList(statement, parameter, rowBounds.unwrap).asScala.toBuffer
  }

  def selectMap[Key,Value](statement : String, mapKey : String) : Map[Key,Value] = {
    sqls.selectMap[Key,Value](statement, mapKey).asScala
  }

  def selectMap[Param,Key,Value](statement : String, parameter : Param, mapKey : String) : Map[Key,Value] = {
    sqls.selectMap[Key,Value](statement, parameter, mapKey).asScala
  }

  def selectMap[Param,Key,Value](statement : String, parameter : Param, mapKey : String, rowBounds : RowBounds) : Map[Key,Value] = {
    sqls.selectMap[Key,Value](statement, parameter, mapKey, rowBounds.unwrap).asScala
  }

  def select[Param, Res](statement : String, parameter : Param, handler : ResultHandler[Res]) : Unit = {
    sqls.select(statement, parameter, handler)
  }

  def select[T](statement : String, handler : ResultHandler[T]) : Unit = {
    sqls.select(statement, handler)
  }

  def select[Param, Res](statement : String, parameter : Param, rowBounds : RowBounds, handler : ResultHandler[Res]) : Unit = {
    sqls.select(statement, parameter, rowBounds.unwrap, handler)
  }

  def insert(statement : String) : Int = {
    sqls.insert(statement)
  }

  def insert[Param](statement : String, parameter : Param) : Int = {
    sqls.insert(statement, parameter)
  }

  def update(statement : String) : Int = {
    sqls.update(statement)
  }

  def update[Param](statement : String, parameter : Param) : Int = {
    sqls.update(statement, parameter)
  }

  def delete(statement : String) : Int = {
    sqls.delete(statement)
  }

  def delete[Param](statement : String, parameter : Param) : Int = {
    sqls.delete(statement, parameter)
  }

  def commit() : Unit = sqls.commit

  def commit(force : Boolean) : Unit = sqls.commit(force)

  def rollback() : Unit = sqls.rollback

  def rollback(force : Boolean) : Unit = sqls.rollback(force)

  def clearCache() : Unit = sqls.clearCache
  
  def flushStatements() : Seq[BatchResult] = sqls.flushStatements.asScala

}
