package org.mybatis.scala.mapping

import org.mybatis.scala.DatabaseSupport
import org.mybatis.scala.domain.User
import org.mybatis.scala.infrastructure.UserRepository
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

/**
 * The specification for [[Insert]].
 */
class InsertSpec extends FlatSpec with Matchers with DatabaseSupport {
  "A Insert" should "insert User into user table" in {
    withReadOnly(Database.default) { implicit session =>
      val expected = User(0, "test", "example@example.com")

      UserRepository.create(expected)

      UserRepository.findById(expected.id) should equal (Some(expected))
    }
  }

  it should "insert User into user table from Tuple2" in {
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
