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

import org.mybatis.scala.session.RowBounds
import org.scalatest._
import org.mybatis.scala.{Database, DatabaseSupport}
import org.mybatis.scala.infrastructure.BlogRepository
import org.mybatis.scala.domain.{Blog, Entry}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification of [[org.mybatis.scala.mapping.SelectList]].
 */
class SelectSpec extends AnyFunSpec with Matchers with DatabaseSupport with BeforeAndAfter {
  describe("selectOne") {
    it("should fetch first record") {
      withReadOnly(Database.default) { implicit session =>
        val expected = Blog("title")
        BlogRepository.create(expected)

        assert(Some(expected) === BlogRepository.findFirst())
      }
    }
  }

  describe("selectOneBy") {
    it("should fetch the record specified by the argument value") {
      withReadOnly(Database.default) { implicit session =>
        val expected = Blog("test")

        BlogRepository.create(expected)

        assert(Some(expected) === BlogRepository.findById(expected.id))
      }
    }

    it("should not fetch any record if there is no matching record in database") {
      withReadOnly(Database.default) { implicit session =>
        assert(None === BlogRepository.findById(100))
      }
    }
  }

  describe("A SelectList") {
    it("should return all records in database") {
      withReadOnly(Database.default) { implicit session =>
        val totalRecords = 10

        for (i <- 1 to totalRecords) {
          BlogRepository.create(Blog(s"$i"))
        }

        assert(totalRecords === BlogRepository.findAll().size)
      }
    }

    it("should return an empty collection if there is no record") {
      withReadOnly(Database.default) { implicit session =>
        assert(Seq.empty[Blog] === BlogRepository.findAll())
      }
    }
  }

  describe("selectListBy") {
    it("should return all records specified by the argument value") {
      withReadOnly(Database.default) { implicit session =>
        val title = "title"
        val totalRecords = 10

        for (_ <- 1 to totalRecords) {
          BlogRepository.create(Blog(title))
        }

        BlogRepository.create(Blog(""))

        val actual = BlogRepository.findAllByTitle(title)

        assert(totalRecords === actual.size)

        actual foreach {
          case Blog(theTitle) => assert(title === theTitle)
        }
      }
    }

    it("should return an empty collection if there is no record") {
      withReadOnly(Database.default) { implicit session =>
        assert(Seq.empty[Blog] === BlogRepository.findAllByTitle("test"))
      }
    }
  }

  describe("selectListPage") {
    it("should fetch records by specifying the offset and limit") {
      withReadOnly(Database.default) { implicit session =>
        for (_ <- 1 to 10) {
          BlogRepository.create(Blog("title"))
        }
        assert(3 === BlogRepository.findAllWithPage(RowBounds(0, 3)).size)
        assert(3 === BlogRepository.findAllWithPage(RowBounds(1, 3)).size)
        assert(2 === BlogRepository.findAllWithPage(RowBounds(8, 3)).size)
      }
    }
  }

  describe("selectListPageBy") {
    it("should fetch records by specifying title, offset, and limit") {
      withReadOnly(Database.default) { implicit session =>
        val title = "title"
        for (_ <- 1 to 10) {
          BlogRepository.create(Blog(title))
        }
        assert(3 === BlogRepository.findAllByTitleWithPage(title, RowBounds(0, 3)).size)
        assert(3 === BlogRepository.findAllByTitleWithPage(title, RowBounds(1, 3)).size)
        assert(2 === BlogRepository.findAllByTitleWithPage(title, RowBounds(8, 3)).size)
      }
    }
  }

  describe("selectMap") {
    it("should fetch records as Map") {
      withReadOnly(Database.default) { implicit session =>
        for (i <- 1 to 10) {
          BlogRepository.create(Blog("title" + i))
        }

        val blogs = BlogRepository.findMapByTitle()
        assert("title1" === blogs("title1").title)
        assert("title2" === blogs("title2").title)
        assert(false === blogs.contains(""))
      }
    }
  }

  describe("selectMapBy") {
    it("should fetch records which starts with new prefix as Map") {
      withReadOnly(Database.default) { implicit session =>
        for (i <- 1 to 10) {
          if (i % 2 == 0) {
            BlogRepository.create(Blog("newtitle"+ i))
          } else {
            BlogRepository.create(Blog("title" + i))
          }
        }

        val blogs = BlogRepository.findMapByTitleWithCondition("new%")
        assert(false === blogs.contains("title1"))
        assert("newtitle2" === blogs("newtitle2").title)
      }
    }
  }

  describe("selectMapPage") {
    it("should fetch records as Map by specifying limit and offset") {
      withReadOnly(Database.default) { implicit session =>
        for (i <- 1 to 10) {
          BlogRepository.create(Blog("title" + i))
        }

        val blogs = BlogRepository.findMapByTitleWithPage(RowBounds(0, 3))
        assert("title1" === blogs("title1").title)
        assert("title2" === blogs("title2").title)
        assert("title3" === blogs("title3").title)
        assert(false === blogs.contains("title4"))
      }
    }
  }

  describe("selectMapPageBy") {
    it("should fetch records as Map by specifying title, limit, and offset") {
      withReadOnly(Database.default) { implicit session =>
        for (i <- 1 to 10) {
          if (i % 2 == 0) {
            BlogRepository.create(Blog("newtitle"+ i))
          } else {
            BlogRepository.create(Blog("title" + i))
          }
        }

        val blogs = BlogRepository.findMapByTitleWithConditionAndPage("new%", RowBounds(0, 3))

        println(blogs)
        assert("newtitle2" === blogs("newtitle2").title)
        assert("newtitle4" === blogs("newtitle4").title)
        assert("newtitle6" === blogs("newtitle6").title)
        assert(false === blogs.contains("newtitle8"))
      }
    }
  }
}
