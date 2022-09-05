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
package org.mybatis.scala.mapping

import org.mybatis.scala.{Database, DatabaseSupport}
import org.mybatis.scala.domain.User
import org.mybatis.scala.infrastructure.UserRepository
import org.scalatest._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification for [[Insert]].
 */
class InsertSpec extends AnyFunSpec with Matchers with DatabaseSupport {
  describe("A Insert") {
    it("should insert User into user table") {
      withReadOnly(Database.default) { implicit session =>
        val expected = User(0, "test", "example@example.com")

        UserRepository.create(expected)

        UserRepository.findById(expected.id) should equal (Some(expected))
      }
    }

    it("should insert User into user table from Tuple2") {
      withReadOnly(Database.default) { implicit session =>
        val name = "test_user"
        val email = "example@example.com"

        UserRepository.createFromTuple2((name, email))

        for {
          id <- UserRepository.lastInsertId()
          actual <- UserRepository.findById(id)
        } yield {
          val expected = User(id, name, email)
          actual should equal (expected)
          Some(expected)
        } getOrElse fail("unable to get last insert id.")
      }
    }
  }
}
