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

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ResultMapSpec extends AnyFunSpec with Matchers {

  case class Address(city: String)
  case class Person(id: Int, name: String, address: Address, tags: List[String])

  describe("ResultMap DSL") {
    it("should build id/result/constructor/association/collection/discriminator mappings") {
      val parent = new ResultMap[Person]()
      val nestedAddress = new ResultMap[Address]()
      val nestedSelect = new SelectOneBy[Int, Address] {
        def xsql = <xsql>SELECT city FROM address</xsql>
      }

      parent.id(property = "id", column = "id", javaType = T[Int], jdbcType = JdbcType.INTEGER)
      parent.result(property = "name", column = "name", javaType = T[String], jdbcType = JdbcType.VARCHAR)
      parent.idArg(column = "id", javaType = T[Int], jdbcType = JdbcType.INTEGER)
      parent.arg(column = "name", javaType = T[String], jdbcType = JdbcType.VARCHAR, select = nestedSelect, resultMap = nestedAddress)
      parent.association[Address](property = "address", column = "address_id", jdbcType = JdbcType.INTEGER, select = nestedSelect, resultMap = nestedAddress, notNullColumn = "address_id", columnPrefix = "addr_")
      parent.associationArg[Address](column = "address_id", jdbcType = JdbcType.INTEGER, select = nestedSelect, resultMap = nestedAddress, notNullColumn = "address_id", columnPrefix = "addr_")
      parent.collection[String](property = "tags", column = "tag", jdbcType = JdbcType.VARCHAR, notNullColumn = "tag", columnPrefix = "t_")
      parent.collectionArg[String](column = "tag", jdbcType = JdbcType.VARCHAR, notNullColumn = "tag", columnPrefix = "t_")

      val c = Case("person", parent.asInstanceOf[ResultMap[Any]])
      parent.discriminator(column = "dtype", javaType = T[String], jdbcType = JdbcType.VARCHAR, cases = Seq(c))

      parent.mappings should have size 6
      parent.constructor should have size 2
      parent.discr._1 shouldBe "dtype"
      parent.discr._2.unwrap shouldBe classOf[String]
      parent.discr._3 shouldBe JdbcType.VARCHAR
      parent.discr._5 should contain(c)
    }

    it("should expose compiled ResultMapping properties and automapping values") {
      val rm = new ResultMap[Person]()
      rm.autoMapping = AutoMappingEnabled
      rm.autoMapping.value shouldBe java.lang.Boolean.TRUE
      rm.autoMapping = AutoMappingDisabled
      rm.autoMapping.value shouldBe java.lang.Boolean.FALSE
      rm.autoMapping = AutoMappingInherited
      rm.autoMapping.value shouldBe null

      rm.id(property = "id", column = "id", javaType = T[Int], jdbcType = JdbcType.INTEGER)
      val idMapping = rm.mappings.head
      idMapping.resultTypeClass shouldBe classOf[Person]
      idMapping.property shouldBe "id"
      idMapping.column shouldBe "id"
      idMapping.javaTypeClass shouldBe classOf[Int]
      idMapping.jdbcTypeEnum shouldBe org.apache.ibatis.`type`.JdbcType.INTEGER
      idMapping.nestedSelect shouldBe null
      idMapping.nestedResultMap shouldBe null
      idMapping.notNullColumn shouldBe null
      idMapping.typeHandlerClass shouldBe null
      idMapping.flags should have size 1
      idMapping.columnPrefix shouldBe null
    }
  }
}
