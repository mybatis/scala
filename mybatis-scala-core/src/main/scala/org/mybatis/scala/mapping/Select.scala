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

import org.mybatis.scala.session.{Session, RowBounds}
import scala.collection.JavaConversions._

sealed trait Select extends Statement {

  var resultMap       : ResultMap[_] = null
  var resultSetType   : ResultSetType = ResultSetType.FORWARD_ONLY
  var fetchSize       : Int = -1
  var useCache        : Boolean = true

  flushCache = false

  def resultTypeClass : Class[_]

}

abstract class SelectList[Param : Manifest, Result : Manifest] extends Select {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply()(implicit s : Session) : List[Result]
    = s.selectList[Result](fqi.id)

  def apply(param : Param)(implicit s : Session) : List[Result]
    = s.selectList[Param,Result](fqi.id, param)

  def apply(param : Param, rowBounds : RowBounds)(implicit s : Session) : List[Result]
    = s.selectList[Param,Result](fqi.id, param, rowBounds)

  def apply(rowBounds : RowBounds)(implicit s : Session) : List[Result]
    = s.selectList[Null,Result](fqi.id, null, rowBounds)

}

abstract class SelectOne[Param : Manifest, Result : Manifest] extends Select {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply()(implicit s : Session) : Result
    = s.selectOne[Result](fqi.id)

  def apply(param : Param)(implicit s : Session) : Result
    = s.selectOne[Param,Result](fqi.id, param)

}

abstract class SelectMap[Param : Manifest, ResultKey, ResultValue : Manifest](mapKey : String) extends Select {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[ResultValue].erasure

  def apply()(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[ResultKey,ResultValue](fqi.id, mapKey)

  def apply(param : Param)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Param,ResultKey,ResultValue](fqi.id, param, mapKey)

  def apply(param : Param, rowBounds : RowBounds)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Param,ResultKey,ResultValue](fqi.id, param, mapKey, rowBounds)

  def apply(rowBounds : RowBounds)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Null,ResultKey,ResultValue](fqi.id, null, mapKey, rowBounds)

}