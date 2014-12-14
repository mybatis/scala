package org.mybatis.scala.mapping

import org.mybatis.scala.config.ConfigurationException
import org.scalatest._
import scala.util.control.NonFatal

/**
 * The specification for [[Statement]].
 */
class StatementSpec extends FunSpec with Matchers {
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
