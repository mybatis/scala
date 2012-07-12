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

/** A wrapper of [[org.apache.ibatis.type.JdbcType]] */
sealed trait JdbcType {
  val unwrap : JdbcTypeEnum
  override def toString = unwrap.toString
}

/** A wrapper of [[org.apache.ibatis.type.JdbcType]] values. */
object JdbcType {
  val ARRAY     = new JdbcType { val unwrap = JdbcTypeEnum.ARRAY }
  val BIT       = new JdbcType { val unwrap = JdbcTypeEnum.BIT }
  val TINYINT   = new JdbcType { val unwrap = JdbcTypeEnum.TINYINT }
  val SMALLINT  = new JdbcType { val unwrap = JdbcTypeEnum.SMALLINT }
  val INTEGER   = new JdbcType { val unwrap = JdbcTypeEnum.INTEGER }
  val BIGINT    = new JdbcType { val unwrap = JdbcTypeEnum.BIGINT }
  val FLOAT     = new JdbcType { val unwrap = JdbcTypeEnum.FLOAT }
  val REAL      = new JdbcType { val unwrap = JdbcTypeEnum.REAL }
  val DOUBLE    = new JdbcType { val unwrap = JdbcTypeEnum.DOUBLE }
  val NUMERIC   = new JdbcType { val unwrap = JdbcTypeEnum.NUMERIC }
  val DECIMAL   = new JdbcType { val unwrap = JdbcTypeEnum.DECIMAL }
  val CHAR      = new JdbcType { val unwrap = JdbcTypeEnum.CHAR }
  val VARCHAR   = new JdbcType { val unwrap = JdbcTypeEnum.VARCHAR }
  val LONGVARCHAR = new JdbcType { val unwrap = JdbcTypeEnum.LONGVARCHAR }
  val DATE      = new JdbcType { val unwrap = JdbcTypeEnum.DATE }
  val TIME      = new JdbcType { val unwrap = JdbcTypeEnum.TIME }
  val TIMESTAMP = new JdbcType { val unwrap = JdbcTypeEnum.TIMESTAMP }
  val BINARY    = new JdbcType { val unwrap = JdbcTypeEnum.BINARY }
  val VARBINARY = new JdbcType { val unwrap = JdbcTypeEnum.VARBINARY }
  val LONGVARBINARY = new JdbcType { val unwrap = JdbcTypeEnum.LONGVARBINARY }
  val NULL      = new JdbcType { val unwrap = JdbcTypeEnum.NULL }
  val OTHER     = new JdbcType { val unwrap = JdbcTypeEnum.OTHER }
  val BLOB      = new JdbcType { val unwrap = JdbcTypeEnum.BLOB }
  val CLOB      = new JdbcType { val unwrap = JdbcTypeEnum.CLOB }
  val BOOLEAN   = new JdbcType { val unwrap = JdbcTypeEnum.BOOLEAN }
  val CURSOR    = new JdbcType { val unwrap = JdbcTypeEnum.CURSOR }
  val UNDEFINED = new JdbcType { val unwrap = JdbcTypeEnum.UNDEFINED }
  val NVARCHAR  = new JdbcType { val unwrap = JdbcTypeEnum.NVARCHAR }
  val NCHAR     = new JdbcType { val unwrap = JdbcTypeEnum.NCHAR }
  val NCLOB     = new JdbcType { val unwrap = JdbcTypeEnum.NCLOB }
  val STRUCT    = new JdbcType { val unwrap = JdbcTypeEnum.STRUCT }
}
