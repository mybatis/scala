/*
 * Copyright 2011 The myBatis Team
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

import scala.collection.mutable.ListBuffer

class ResultMap[ResultType : Manifest](val parent : ResultMap[_] = null) {

  private[scala] val mappings = new ListBuffer[ResultMapping]
  private[scala] val constructor = new ListBuffer[ResultMapping]
  private[scala] var discr : (String, T[_], JdbcType, T[_ <: TypeHandler[_]], Seq[Case]) = null

  var fqi : FQI = null

  def resultTypeClass = manifest[ResultType].erasure

  def id(
    property : String = null,
    column : String = null,
    javaType : T[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(
      T[ResultType],
      property,
      column,
      javaType,
      jdbcType,
      null,
      null,
      null,
      typeHandler,
      Seq(ResultFlag.ID))

  }

  def result(
    property : String = null,
    column : String = null,
    javaType : T[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(
      T[ResultType],
      property,
      column,
      javaType,
      jdbcType,
      null,
      null,
      null,
      typeHandler,
      Seq())
  }

  def arg(
    column : String = null,
    javaType : T[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    constructor += new ResultMapping(
      T[ResultType],
      null,
      column,
      javaType,
      jdbcType,
      null,
      null,
      null,
      typeHandler,
      Seq(ResultFlag.CONSTRUCTOR))
  }

  def idArg(
    column : String = null,
    javaType : T[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    select : Select = null,
    resultMap : ResultMap[_] = null,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    constructor += new ResultMapping(
      T[ResultType],
      null,
      column,
      javaType,
      jdbcType,
      select,
      resultMap,
      null,
      typeHandler,
      Seq(ResultFlag.CONSTRUCTOR, ResultFlag.ID))
  }

  def association[Type : Manifest](
    property : String = null,
    column : String = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    select : Select = null,
    resultMap : ResultMap[_] = null,
    notNullColumn : String = null,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(
      T[ResultType],
      property,
      column,
      T[Type],
      jdbcType,
      select,
      resultMap,
      notNullColumn,
      typeHandler,
      Seq())

  }

  def collection[Type : Manifest](
    property : String = null,
    column : String = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    select : Select = null,
    resultMap : ResultMap[_] = null,
    notNullColumn : String = null,
    typeHandler : T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(
      T[ResultType],
      property,
      column,
      T[java.util.List[Type]],
      jdbcType,
      select,
      resultMap,
      notNullColumn,
      typeHandler,
      Seq())

  }

  def discriminator(
    column : String = null,
    javaType : T[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : T[_ <: TypeHandler[_]] = null,
    cases : Seq[Case] = Seq()) = {

    discr = (column, javaType, jdbcType, typeHandler, cases)

  }

}
