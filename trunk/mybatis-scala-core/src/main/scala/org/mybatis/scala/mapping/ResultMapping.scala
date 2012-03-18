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

import org.apache.ibatis.`type`.{JdbcType => JdbcTypeEnum}
import org.apache.ibatis.mapping.{ResultFlag => ResultFlagEnum}
import java.util.{List => JList}
import scala.collection.JavaConversions._

private[scala] class ResultMapping (
  resultType : T[_],
  property_ : String,
  column_ : String,
  javaType : T[_],
  jdbcType : JdbcType,
  nestedSelect_ : Select,
  nestedResultMap_ : ResultMap[_],
  notNullColumn_ : String,
  columnPrefix_ : String,
  typeHandler : T[_ <: TypeHandler[_]],
  flags_ : Seq[ResultFlag] = Seq()
) {

  def resultTypeClass : Class[_] = resultType.unwrap
  def property : String = property_
  def column : String = column_
  def javaTypeClass : Class[_] = if (javaType == null || javaType.isVoid) null else javaType.unwrap
  def jdbcTypeEnum : JdbcTypeEnum = if (jdbcType == null || jdbcType == JdbcType.UNDEFINED) null else jdbcType.unwrap
  def nestedSelect : Select = nestedSelect_
  def nestedResultMap : ResultMap[_] = nestedResultMap_
  def notNullColumn : String = notNullColumn_
  def typeHandlerClass : Class[_ <: TypeHandler[_]] = if (typeHandler == null) null else typeHandler.unwrap
  def flags : JList[ResultFlagEnum] = for (f <- flags_) yield f.unwrap
  def columnPrefix = columnPrefix_
}
