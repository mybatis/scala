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
package org.mybatis.scala.infrastructure

import org.mybatis.scala.domain.User
import org.mybatis.scala.mapping._
import org.mybatis.scala.mapping.Binding._
import scala.language.postfixOps

object UserRepository {
  val defaultResultMap = new ResultMap[User] {
    idArg(column = "id", javaType = T[Int])
    arg(column = "name", javaType = T[String])
    arg(column = "email", javaType = T[String])
  }

  val create = new Insert[User] {
    keyGenerator = JdbcGeneratedKey(null, "id")
    def xsql = <xsql>INSERT INTO user(name, email) VALUES({"name" ?}, {"email" ?})</xsql>
  }

  val createFromTuple2 = new Insert[(String, String)] {
    def xsql = <xsql>INSERT INTO user(name, email) VALUES({"_1" ?}, {"_2" ?})</xsql>
  }

  val findById = new SelectOneBy[Int, User] {
    resultMap = defaultResultMap
    def xsql = <xsql>SELECT * FROM user WHERE id = {"id" ?}</xsql>
  }

  val findAll = new SelectList[User] {
    resultMap = defaultResultMap
    def xsql = <xsql>SELECT * FROM user</xsql>
  }

  val lastInsertId = new SelectOne[Int] {
    def xsql = <xsql>CALL IDENTITY()</xsql>
  }

  def bind = Seq(create, createFromTuple2, findById, findAll, lastInsertId)
}
