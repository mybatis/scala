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

/** Base class for all Select statements.
  * @version \$Revision$
  */
sealed trait Select extends Statement {

  /** A reference to an external resultMap.
    * Result maps are the most powerful feature of MyBatis, and with a good understanding of them,
    * many difficult mapping cases can be solved.
    */
  var resultMap       : ResultMap[_] = null

  /** Any one of FORWARD_ONLY|SCROLL_SENSITIVE|SCROLL_INSENSITIVE. Default FORWARD_ONLY. */
  var resultSetType   : ResultSetType = ResultSetType.FORWARD_ONLY

  /** This is a driver hint that will attempt to cause the driver to return results in batches of rows
    * numbering in size equal to this setting. Default is unset (driver dependent).
    */
  var fetchSize       : Int = -1

  /** Setting this to true will cause the results of this statement to be cached.
    * Default: true for select statements.
    */
  var useCache        : Boolean = true

  flushCache = false

  def resultTypeClass : Class[_]

}

/** The difference between SelectOne and SelectList is only in that SelectOne must return exactly one object
  * or null (none). If any more than one, an exception will be thrown.
  *
  * If you don’t know how many objects are expected, use selectList.
  *
  * If you want to check for the existence of an object, you’re better off returning a count (0 or 1).
  *
  * The SelectMap is a special case in that it is designed to convert a list of results into a Map based on
  * one of the properties in the resulting objects.
  *
  * Because not all statements require a parameter, these methods are overloaded with versions that do not require the parameter object.
  *
  * == Details ==
  * This class defines a function: ((param : Param, rowBounds : RowBounds) => List[Result]) where param and rowBounds are optional.
  *
  * == Sample code ==
  * {{{
  *   val findAll = new SelectList[Nothing,Person] {
  *     def xsql = "SELECT * FROM person ORDER BY name"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findAll()
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam Result retult type
  */
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

/** The difference between SelectOne and SelectList is only in that SelectOne must return exactly one object
  * or null (none). If any more than one, an exception will be thrown.
  *
  * If you don’t know how many objects are expected, use selectList.
  *
  * If you want to check for the existence of an object, you’re better off returning a count (0 or 1).
  *
  * The SelectMap is a special case in that it is designed to convert a list of results into a Map based on
  * one of the properties in the resulting objects.
  *
  * Because not all statements require a parameter, these methods are overloaded with versions that do not require the parameter object.
  *
  * == Details ==
  * This class defines a function: ((param : Param) => Result) where param is optional.
  *
  * == Sample code ==
  * {{{
  *   val find = new SelectOne[Int,Person] {
  *     def xsql = "SELECT * FROM person WHERE id = #{{id}}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val p = find(1)
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam Result retult type
  */
abstract class SelectOne[Param : Manifest, Result : Manifest] extends Select {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply()(implicit s : Session) : Option[Result] = {
    val r = s.selectOne[Result](fqi.id);
    if (r == null) None else Some(r)
  }

  def apply(param : Param)(implicit s : Session) : Option[Result] = {
    val r = s.selectOne[Param,Result](fqi.id, param)
    if (r == null) None else Some(r)
  }

}

/** The difference between SelectOne and SelectList is only in that SelectOne must return exactly one object
  * or null (none). If any more than one, an exception will be thrown.
  *
  * If you don’t know how many objects are expected, use selectList.
  *
  * If you want to check for the existence of an object, you’re better off returning a count (0 or 1).
  *
  * The SelectMap is a special case in that it is designed to convert a list of results into a Map based on
  * one of the properties in the resulting objects.
  *
  * Because not all statements require a parameter, these methods are overloaded with versions that do not require the parameter object.
  *
  * == Details ==
  * This class defines a function: ((param : Param) => Map[ResultKey, ResultValue]) where param is optional.
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMap[Nothing,Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById()
  *     val p = people(5)
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam ResultKey map Key type
  * @tparam ResultValue map Value type
  * @param mapKey Property to be used as map key
  */
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