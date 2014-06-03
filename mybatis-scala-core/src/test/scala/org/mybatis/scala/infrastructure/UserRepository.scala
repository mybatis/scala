package org.mybatis.scala.infrastructure

import org.mybatis.scala.domain.User
import org.mybatis.scala.mapping._
import org.mybatis.scala.mapping.Binding._

object UserRepository {
  val defaultResultMap = new ResultMap[User] {
    idArg(column = "id", javaType = T[Int])
    arg(column = "name", javaType = T[String])
    arg(column = "email", javaType = T[String])
  }

  val create = new Insert[User] {
    keyGenerator = JdbcGeneratedKey(null, "id")
    def xsql = <xsql>INSERT INTO user(name, email) VALUES({"name" ?}, {"email" ?})</xsql>
  }

  val createFromTuple2 = new Insert[(String, String)] {
    keyGenerator = JdbcGeneratedKey(null, "id")
    def xsql = <xsql>INSERT INTO user(name, email) VALUES({"_1" ?}, {"_2" ?})</xsql>
  }

  val findById = new SelectOneBy[Int, User] {
    resultMap = defaultResultMap
    def xsql = <xsql>SELECT * FROM user WHERE id = {"id" ?}</xsql>
  }

  val findAll = new SelectList[User] {
    resultMap = defaultResultMap
    def xsql = <xsql>SELECT * FROM user</xsql>
  }

  val lastInsertId = new SelectOne[Int] {
    def xsql = <xsql>CALL IDENTITY()</xsql>
  }

  def bind = Seq(create, createFromTuple2, findById, findAll, lastInsertId)
}
