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

import org.mybatis.scala.mapping._
import org.mybatis.scala.mapping.Binding._
import org.mybatis.scala.domain.{Entry, Blog}
import org.mybatis.scala.session.Session
import scala.language.postfixOps

object BlogRepository {
  lazy val blogResultMap = new ResultMap[Blog] {
    id(column = "blog_id", property = "id")
    arg(column = "blog_title", javaType = T[String])
    collection(property = "entries", resultMap = entryResultMap)
  }

  lazy val entryResultMap = new ResultMap[Entry] {
    id(column = "entry_id", property = "id")
    arg(column = "entry_body", javaType = T[String])
  }

  val findFirst = new SelectOne[Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
        ORDER BY b.id
        LIMIT 1
      </xsql>
  }

  val findById = new SelectOneBy[Int, Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
          b.id as blog_id,
          b.title as blog_title,
          e.id as entry_id,
          e.body as entry_body
        FROM
          blog b
        LEFT OUTER JOIN
          entry e
        ON
          e.blog_id = b.id
        WHERE
          b.id = {"id" ?}
      </xsql>
  }

  val findAll = new SelectList[Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
          b.id as blog_id,
          b.title as blog_title,
          e.id as entry_id,
          e.body as entry_body
        FROM
          blog b
        LEFT OUTER JOIN
          entry e
        ON
          e.blog_id = b.id
      </xsql>
  }

  val findAllByTitle = new SelectListBy[String, Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
          b.id as blog_id,
          b.title as blog_title,
          e.id as entry_id,
          e.body as entry_body
        FROM
          blog b
        LEFT OUTER JOIN
          entry e
        ON
          e.blog_id = b.id
        WHERE
          b.title = {"title" ?}
      </xsql>
  }

  val findAllWithPage = new SelectListPage[Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
      </xsql>
  }

  val findAllByTitleWithPage = new SelectListPageBy[String, Blog] {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
        WHERE
        b.title = {"title" ?}
      </xsql>
  }

  val findMapByTitle = new SelectMap[String, Blog](mapKey = "title") {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
      </xsql>
  }

  val findMapByTitleWithPage = new SelectMapPage[String, Blog](mapKey = "title") {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
      </xsql>
  }

  val findMapByTitleWithCondition = new SelectMapBy[String, String, Blog](mapKey = "title") {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
        WHERE
        b.title LIKE {"title" ?}
      </xsql>
  }

  val findMapByTitleWithConditionAndPage = new SelectMapPageBy[String, String, Blog](mapKey = "title") {
    resultMap = blogResultMap
    def xsql =
      <xsql>
        SELECT
        b.id as blog_id,
        b.title as blog_title,
        e.id as entry_id,
        e.body as entry_body
        FROM
        blog b
        LEFT OUTER JOIN
        entry e
        ON
        e.blog_id = b.id
        WHERE
        b.title LIKE {"title" ?}
        ORDER BY b.id ASC
      </xsql>
  }

  val insertBlog = new Insert[Blog] {
    keyGenerator = JdbcGeneratedKey(null, "id")
    def xsql = <xsql>INSERT INTO blog(title) VALUES({"title" ?})</xsql>
  }
  
  val insertEntry = new Insert[Entry] {
    keyGenerator = JdbcGeneratedKey(null, "id")
    def xsql = <xsql>INSERT INTO entry(body) VALUES({"body" ?})</xsql>
  }

  def create(blog: Blog)(implicit session: Session) = {
    insertBlog(blog)
    blog.entries.foreach(e => insertEntry(e))
  }

  def bind = Seq(
    insertBlog, insertEntry, findById, findAll, findAllByTitle,
    findAllWithPage, findAllByTitleWithPage, findFirst, findMapByTitle,
    findMapByTitleWithCondition, findMapByTitleWithPage, findMapByTitleWithConditionAndPage
  )
}
