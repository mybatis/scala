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
package org.mybatis.scala.config

import org.apache.ibatis.session.{Configuration => MBConfig}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification for [[DynamicSQLBuilder]].
 */
class DynamicSQLBuilderSpec extends AnyFunSpec with Matchers {

  private def buildSQL(xsql: scala.xml.Node): Unit = {
    val mbConfig = new MBConfig()
    new DynamicSQLBuilder(mbConfig, xsql).build
  }

  describe("DynamicSQLBuilder") {

    describe("choose element") {
      it("should accept XML whitespace between when/otherwise tags") {
        buildSQL(
          <xsql>SELECT 1 FROM dual<choose><when test="true">1</when><otherwise>2</otherwise></choose></xsql>
        )
      }

      it("should accept newlines and spaces between when/otherwise tags") {
        buildSQL(
          <xsql>
            SELECT 1 FROM dual
            <choose>
              <when test="true">1</when>
              <otherwise>2</otherwise>
            </choose>
          </xsql>
        )
      }

      it("should throw ConfigurationException when non-whitespace text appears inside choose") {
        intercept[ConfigurationException] {
          buildSQL(
            <xsql>SELECT 1 FROM dual<choose>invalid text<when test="true">1</when></choose></xsql>
          )
        }
      }

      it("should throw ConfigurationException when XML elements other than when/otherwise appear inside choose") {
        intercept[ConfigurationException] {
          buildSQL(
            <xsql>SELECT 1 FROM dual<choose><if test="true">nested</if><when test="true">1</when></choose></xsql>
          )
        }
      }

      it("should throw ConfigurationException when more than one otherwise element appears in choose") {
        intercept[ConfigurationException] {
          buildSQL(
            <xsql><choose><when test="true">1</when><otherwise>a</otherwise><otherwise>b</otherwise></choose></xsql>
          )
        }
      }
    }

    describe("if element") {
      it("should build an if node") {
        buildSQL(<xsql>SELECT 1 FROM dual<if test="true"> AND 1=1</if></xsql>)
      }
    }

    describe("where element") {
      it("should build a where node") {
        buildSQL(
          <xsql>
            SELECT * FROM user
            <where>
              <if test="id != null">AND id = {"{id}"}</if>
            </where>
          </xsql>
        )
      }
    }

    describe("set element") {
      it("should build a set node") {
        buildSQL(
          <xsql>
            UPDATE user
            <set>
              <if test="name != null">name = {"{name}"},</if>
            </set>
            WHERE id = {"{id}"}
          </xsql>
        )
      }
    }

    describe("trim element") {
      it("should build a trim node with prefix/suffix") {
        buildSQL(
          <xsql>
            SELECT * FROM user
            <trim prefix="WHERE" prefixOverrides="AND |OR ">
              <if test="name != null">AND name = {"{name}"}</if>
            </trim>
          </xsql>
        )
      }
    }

    describe("foreach element") {
      it("should build a foreach node") {
        buildSQL(
          <xsql>
            SELECT * FROM user WHERE id IN
            <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
              {"{item}"}
            </foreach>
          </xsql>
        )
      }
    }

    describe("bind element") {
      it("should build a bind node") {
        buildSQL(
          <xsql>
            <bind name="pattern" value="'%' + name + '%'"/>
            SELECT * FROM user WHERE name LIKE {"{pattern}"}
          </xsql>
        )
      }
    }

    describe("when element at top level") {
      it("should build a when node used directly") {
        buildSQL(<xsql><when test="true">text</when></xsql>)
      }
    }

    describe("otherwise element at top level") {
      it("should build an otherwise node used directly") {
        buildSQL(<xsql><otherwise>text</otherwise></xsql>)
      }
    }

    describe("CDATA sections") {
      it("should build a text node from CDATA") {
        buildSQL(<xsql>{scala.xml.PCData("SELECT 1 FROM dual WHERE 1 < 2")}</xsql>)
      }
    }

    describe("error cases") {
      it("should throw ConfigurationException for unknown XML elements") {
        intercept[ConfigurationException] {
          buildSQL(<xsql><unknowntag>text</unknowntag></xsql>)
        }
      }
    }
  }
}

