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

import org.mybatis.scala.config.ConfigurationException
import org.scalatest._
import scala.util.control.NonFatal
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * The specification for [[Statement]].
 */
class StatementSpec extends AnyFunSpec with Matchers {
  val simpleStatement = new Statement {
  override def parameterTypeClass: Class[_] = classOf[Unit]
    override def xsql: XSQL = <xsql>SELECT 1</xsql>
  }

  describe("A Statement") {
    it("should throw an exception if FQI isn't set") {
      intercept[ConfigurationException] {
        simpleStatement.execute {
          fail("should not come here")
        }
      }
    }

    it("should not throw any exception if FQI is set") {
      try {
        simpleStatement.fqi = new FQI("databases", "local")
        simpleStatement.execute {}
      } catch {
        case NonFatal(e) => fail(e.getMessage)
      }
    }
    it("should return XSQL") {
      simpleStatement.xsql should equal (<xsql>SELECT 1</xsql>)
    }
  }
}
