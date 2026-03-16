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

import org.mybatis.scala.mapping.Binding._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification for [[Binding]].
 */
class BindingSpec extends AnyFunSpec with Matchers {

  describe("Binding.?") {
    it("should generate a simple property binding") {
      val result = ?("name")
      result should equal ("#{name}")
    }

    it("should include jdbcType when specified") {
      val result = ?("name", jdbcType = JdbcType.VARCHAR)
      result should equal ("#{name,jdbcType=VARCHAR}")
    }

    it("should include jdbcTypeName when specified") {
      val result = ?("name", jdbcTypeName = "NVARCHAR")
      result should equal ("#{name,jdbcTypeName=NVARCHAR}")
    }

    it("should include numericScale when non-zero") {
      val result = ?("amount", numericScale = 2)
      result should equal ("#{amount,numericScale=2}")
    }

    it("should include mode when not ModeIN") {
      val result = ?("param", mode = ModeOUT)
      result should equal ("#{param,mode=OUT}")
    }

    it("should include mode INOUT") {
      val result = ?("param", mode = ModeINOUT)
      result should equal ("#{param,mode=INOUT}")
    }

    it("should include javaType for a reference type") {
      val result = ?("name", javaType = classOf[String])
      result should equal ("#{name,javaType=java.lang.String}")
    }

    it("should include javaType for a primitive type with underscore alias") {
      val result = ?("count", javaType = classOf[Int])
      result should equal ("#{count,javaType=_int}")
    }

    it("should include javaType from T[_].unwrap") {
      val result = ?("name", javaType = T[String].unwrap)
      result should equal ("#{name,javaType=java.lang.String}")
    }

    it("should combine multiple parameters") {
      val result = ?("amount", jdbcType = JdbcType.DECIMAL, numericScale = 2)
      result should equal ("#{amount,jdbcType=DECIMAL,numericScale=2}")
    }

    it("should not include mode when ModeIN (default)") {
      val result = ?("param", mode = ModeIN)
      result should equal ("#{param}")
    }
  }

  describe("Binding.Param") {
    it("should generate a simple binding using postfix ? syntax") {
      val param = new Param("name")
      param.? should equal ("#{name}")
    }
  }

  describe("T") {
    it("should provide raw class") {
      T[String].raw should equal (classOf[String])
    }

    it("should provide unwrapped class") {
      T[String].unwrap should equal (classOf[String])
    }

    it("should correctly identify non-void types") {
      T[String].isVoid should equal (false)
    }

    it("should correctly identify void type") {
      T[Unit].isVoid should equal (true) // Scala Unit maps to java.lang.Void.TYPE at runtime
    }
  }
}
