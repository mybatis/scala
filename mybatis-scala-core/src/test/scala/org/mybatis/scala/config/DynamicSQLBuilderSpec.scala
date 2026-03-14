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
    }
  }
}
