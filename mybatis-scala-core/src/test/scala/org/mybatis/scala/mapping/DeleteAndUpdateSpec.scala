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
package org.mybatis.scala.mapping

import org.mybatis.scala.{Database, DatabaseSupport}
import org.mybatis.scala.domain.User
import org.mybatis.scala.infrastructure.UserRepository
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification for [[Delete]] and [[Update]].
 */
class DeleteAndUpdateSpec extends AnyFunSpec with Matchers with DatabaseSupport {

  describe("A Delete") {
    it("should delete User from user table") {
      withReadOnly(Database.default) { implicit session =>
        val user = User(0, "to_delete", "delete@example.com")
        UserRepository.create(user)
        UserRepository.findById(user.id) should equal (Some(user))

        UserRepository.deleteById(user.id)
        UserRepository.findById(user.id) should equal (None)
      }
    }

    it("should return zero affected rows when no record matches") {
      withReadOnly(Database.default) { implicit session =>
        val affected = UserRepository.deleteById(99999)
        affected should equal (0)
      }
    }
  }

  describe("An Update") {
    it("should update User in user table") {
      withReadOnly(Database.default) { implicit session =>
        val user = User(0, "original", "orig@example.com")
        UserRepository.create(user)

        val updated = User(user.id, "updated", "updated@example.com")
        UserRepository.updateUser(updated)

        UserRepository.findById(user.id) should equal (Some(updated))
      }
    }

    it("should return zero affected rows when no record matches") {
      withReadOnly(Database.default) { implicit session =>
        val affected = UserRepository.updateUser(User(99999, "nobody", "nobody@example.com"))
        affected should equal (0)
      }
    }
  }
}
