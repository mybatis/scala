/*
 * Copyright 2011-2012 The myBatis Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mybatis.scala.mapping

sealed class ParamModeEnum(v : String) {
  override def toString : String = v
}

case object ModeIN extends ParamModeEnum("IN")
case object ModeOUT extends ParamModeEnum("OUT")
case object ModeINOUT extends ParamModeEnum("INOUT")

/** Provides compile time checked inline parameter bindings.
  * == Syntax ==
  * General notation
  * {{{
  *   {?[T](propertyName, jdbcType=DBT, typeHandler=T[TH], mode=MD, numericScale=NS, resultMap=RM)}
  * }}}
  * Where
  * {{{
  *  - T                : JavaType      : Type of the parameter property, Optional
  *  - propertyName     : String        : Name of the parameter property, Required
  *  - DBT              : JdbcType      : Any value of org.mybatis.scala.mapping.JdbcType, Optional
  *  - TH               : TypeHandler   : A TypeHandler implementation class, Optional
  *  - MD               : ParamModeEnum : Any of (ModeIN, ModeOUT, ModeINOUT), Optional
  *  - NS               : Int           : Numeric Scale, Optional
  *  - RM               : ResultMap     : A ResultMap Object, Optional
  * }}}
  *
  * Simplified Notation
  * If you need to specify only the property name, you ca use this simplified notation:
  * {{{
  *   {propertyName?}
  * }}}
  *
  * == Code Examples ==
  * {{{
  *   <xsql>
  *      SELECT * FROM person
  *      WHERE name = {"name"?}
  *   </xsql>
  * }}}
  *
  * {{{
  *   <xsql>
  *      SELECT * FROM task
  *      WHERE due_date = {?("date", typeHandler=T[JodaTimeTypeHandler])}
  *   </xsql>
  * }}}
  *
  */
object Binding {  

  /** Custom Aliased Types */
  private val valueTypes = Set("byte", "long", "short", "int", "double", "float", "boolean",
    "byte[]", "long[]", "short[]", "int[]", "double[]", "float[]", "boolean[]")

  /** Custom alias translator */
  private def translate(cls : Class[_]) : String = {
    if (valueTypes contains cls.getSimpleName) "_" + cls.getSimpleName
    else cls.getName
  }

  /** Generates an inline parameter binding */
  def ?[JavaType : Manifest] (
    property : String,
    jdbcType : JdbcType = null,
    jdbcTypeName : String = null,
    numericScale : Int = 0,
    mode : ParamModeEnum = ModeIN,
    typeHandler : T[_ <: TypeHandler[_]] = null,
    resultMap : ResultMap[_] = null
  ) : String = {  
    Seq[Option[String]](
      Some(property)
      ,if (jdbcType != null) Some("jdbcType=" + jdbcType.toString) else None
      ,if (jdbcTypeName != null) Some("jdbcTypeName=" + jdbcTypeName) else None
      ,if (numericScale != 0) Some("numericScale=" + numericScale) else None
      ,if (mode != ModeIN) Some("mode=" + mode.toString) else None
      ,if (typeHandler != null) Some("typeHandler=" + typeHandler.unwrap.getName) else None
      ,if (resultMap != null) Some("resultMap=" + resultMap.fqi.id) else None
      ,{
        val t = manifest[JavaType].erasure
        if (t != classOf[Object]) Some("javaType=" + translate(t)) else None
      }
      ) filter {_ match {case Some(x) => true; case None => false }} map {_.get} mkString("#{", ",", "}")
  }

  /** Utility class for simplified syntax support */
  case class Param(property : String) {
    def ? = Binding ? (property)
  }

  /** Implicit conversion for simplified syntax support */
  implicit def StringToParam(s : String) = new Param(s)

}