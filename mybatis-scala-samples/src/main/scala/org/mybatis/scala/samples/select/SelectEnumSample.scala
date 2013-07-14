package org.mybatis.scala.samples.select

import org.mybatis.scala.mapping.EnumStrTypeHandler
import org.mybatis.scala.mapping.EnumIntTypeHandler
import org.mybatis.scala.mapping.SelectList
import org.mybatis.scala.mapping.ResultMap
import org.mybatis.scala.mapping._
import org.mybatis.scala.samples.util.DBSchema
import org.mybatis.scala.samples.util.DBSampleData

/*
 * sample enum
 */
object GroupEnum extends Enumeration {
  val Customers = Value(1, "Customers")
  val Suppliers = Value(2, "Suppliers")
  val Employees = Value(3, "Employees")
}

/*
 * sample case class to represent the selected records
 */
case class Group(val id: Int, val name: String, val group: GroupEnum.Value)

/*
 * string based handler for GroupEnum
 */
class GroupEnumStrTypeHandler extends EnumStrTypeHandler {
  def enumObj = GroupEnum
}

/*
 * int based handler for GroupEnum
 */
class GroupEnumIntTypeHandler extends EnumIntTypeHandler {
  def enumObj = GroupEnum
}

/*
 * the queries
 */
object GroupQueries {
  class Query(colName: String, typeHandler: T[_ <: TypeHandler[Enumeration#Value]]) extends SelectList[Group] {
    resultMap = new ResultMap[Group] {
      arg("id_", javaType = T[Int])
      arg("name_", javaType = T[String])
      /*
       * an enum field, javaType is mandatory in this case since the typeHandler val is typed erased (or something...) 
       */
      arg(colName, typeHandler = typeHandler, javaType = T[Enumeration#Value])
    }
    def xsql = """ 
      SELECT
          id_, name_
        FROM
          people_group
      """
  }
  /*
   * the actual queries
   */
  val selectEnumViaString = new Query("name_", T[GroupEnumStrTypeHandler])
  val selectEnumViaInteger = new Query("id_", T[GroupEnumIntTypeHandler])

}

object SelectEnumSample extends App {
  // Do the Magic ...
  override def main(args: Array[String]): Unit = {
    /*
     * this is a bit tricky, getting to register the statements before actually creating the Configuration object
     */
    CDB.ConfigurationSpec.statements(GroupQueries.selectEnumViaString, GroupQueries.selectEnumViaInteger)
    CDB.context.transaction { implicit s =>

      // Create database and populate it with sample data
      DBSchema.create
      DBSampleData.populate

      for (q <- Seq(GroupQueries.selectEnumViaString, GroupQueries.selectEnumViaInteger)) {
        // Query
        GroupQueries.selectEnumViaString().foreach(x => {
          println(x)
          //some basic tests
          assert(x.group != null)
          assert(x.group.id == x.id)
          assert(x.group.toString == x.name)
          assert(GroupEnum(x.id) == x.group)
          assert(GroupEnum.withName(x.name) == x.group)
        })
      }
    }
  }
}