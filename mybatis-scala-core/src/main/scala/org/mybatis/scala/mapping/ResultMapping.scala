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

import org.apache.ibatis.`type`.{JdbcType => JdbcTypeEnum}
import org.apache.ibatis.mapping.{ResultFlag => ResultFlagEnum}
import java.util.{List => JList}
import scala.jdk.CollectionConverters.*

private[scala] class ResultMapping (
  resultType : T[?],
  property_ : String,
  column_ : String,
  javaType : T[?],
  jdbcType : JdbcType,
  nestedSelect_ : Select,
  nestedResultMap_ : ResultMap[?],
  notNullColumn_ : String,
  columnPrefix_ : String,
  typeHandler : T[? <: TypeHandler[?]],
  flags_ : Seq[ResultFlag] = Seq()
) {

  def resultTypeClass : Class[?] = resultType.unwrap
  def property : String = property_
  def column : String = column_
  def javaTypeClass : Class[?] = if (javaType == null || javaType.isVoid) null else javaType.unwrap
  def jdbcTypeEnum : JdbcTypeEnum = if (jdbcType == null || jdbcType == JdbcType.UNDEFINED) null else jdbcType.unwrap
  def nestedSelect : Select = nestedSelect_
  def nestedResultMap : ResultMap[?] = nestedResultMap_
  def notNullColumn : String = notNullColumn_
  def typeHandlerClass : Class[? <: TypeHandler[?]] = if (typeHandler == null) null else typeHandler.unwrap
  def flags : JList[ResultFlagEnum] = (for (f <- flags_) yield f.unwrap).asJava
  def columnPrefix = columnPrefix_
}
