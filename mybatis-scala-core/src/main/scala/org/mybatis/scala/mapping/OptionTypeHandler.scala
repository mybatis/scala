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
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

/** Generic scala.Option[T] TypeHandler.
  * Wraps any TypeHandler[T] to support TypeHandler[Option[T]]
  * == Example ==
  * {{{
  *   class OptionIntegerTypeHandler extends OptionTypeHandler(new IntegerTypeHandler())
  * }}}
  */
class OptionTypeHandler[T](delegate : TypeHandler[T]) extends TypeHandler[Option[T]] {

  def setParameter(ps : PreparedStatement, i : Int, parameter : Option[T], jdbcType : JdbcTypeEnum) : Unit = 
    parameter match {
      case None => delegate.setParameter(ps, i, null.asInstanceOf[T], jdbcType)
      case Some(v) => delegate.setParameter(ps, i, v, jdbcType)
    }

  def getResult(rs : ResultSet, columnName : String) : Option[T] =
    delegate.getResult(rs, columnName) match {
      case null => None
      case v : T => Some(v)
    }

  def getResult(rs : ResultSet, columnIndex : Int) : Option[T]  =
    delegate.getResult(rs, columnIndex) match {
      case null => None
      case v : T => Some(v)
    }

  def getResult(cs : CallableStatement, columnIndex : Int) : Option[T] = 
    delegate.getResult(cs, columnIndex) match {
      case null => None
      case v : T => Some(v)
    }

}

/** Builtin Option TypeHandlers */
object TypeHandlers {
  
  import org.apache.ibatis.`type`._
  
  class OptBooleanTypeHandler     extends OptionTypeHandler(new BooleanTypeHandler())
  class OptByteTypeHandler        extends OptionTypeHandler(new ByteTypeHandler())
  class OptShortTypeHandler       extends OptionTypeHandler(new ShortTypeHandler())
  class OptIntegerTypeHandler     extends OptionTypeHandler(new IntegerTypeHandler())
  class OptLongTypeHandler        extends OptionTypeHandler(new LongTypeHandler())
  class OptFloatTypeHandler       extends OptionTypeHandler(new FloatTypeHandler())
  class OptDoubleTypeHandler      extends OptionTypeHandler(new DoubleTypeHandler())
  class OptStringTypeHandler      extends OptionTypeHandler(new StringTypeHandler())
  class OptClobTypeHandler        extends OptionTypeHandler(new ClobTypeHandler())
  class OptBlobTypeHandler        extends OptionTypeHandler(new BlobTypeHandler())
  class OptNStringTypeHandler     extends OptionTypeHandler(new NStringTypeHandler())
  class OptNClobTypeHandler       extends OptionTypeHandler(new NClobTypeHandler())
  class OptBigDecimalTypeHandler  extends OptionTypeHandler(new BigDecimalTypeHandler())
  class OptDateTypeHandler        extends OptionTypeHandler(new DateOnlyTypeHandler())
  class OptTimeTypeHandler        extends OptionTypeHandler(new TimeOnlyTypeHandler())
  class OptTimestampTypeHandler   extends OptionTypeHandler(new DateTypeHandler())

}

