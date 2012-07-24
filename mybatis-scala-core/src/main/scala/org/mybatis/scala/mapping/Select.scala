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

import org.mybatis.scala.session.{Session, RowBounds, ResultHandlerDelegator, ResultContext}
import scala.collection.mutable._;

/** Base class for all Select statements.
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

/** Query for a list of objects.
  *
  * == Details ==
  * This class defines a function: (=> List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findAll = new SelectList[Person] {
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
  * @tparam Result retult type
  */
abstract class SelectList[Result : Manifest] 
  extends Select 
  with SQLFunction0[Seq[Result]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[Result].erasure

  def apply()(implicit s : Session) : Seq[Result]
    = s.selectList[Result](fqi.id)

  def handle(callback : ResultContext => Unit)(implicit s : Session) : Unit = {
    s.select(fqi.id, new ResultHandlerDelegator(callback))
  }

}

/** Query for a list of objects using the input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findByName = new SelectListBy[String,Person] {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findByName("John%")
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam Result retult type
  */
abstract class SelectListBy[Param : Manifest, Result : Manifest] 
  extends Select 
  with SQLFunction1[Param, Seq[Result]] {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply(param : Param)(implicit s : Session) : Seq[Result]
    = s.selectList[Param,Result](fqi.id, param)

  def handle(param : Param, callback : ResultContext => Unit)(implicit s : Session) : Unit = {
    s.select(fqi.id, param, new ResultHandlerDelegator(callback))
  }

}

/** Query for a list of objects with RowBounds.
  *
  * == Details ==
  * This class defines a function: (RowBounds => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findAll = new SelectListPage[Person] {
  *     def xsql = "SELECT * FROM person ORDER BY name"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findAll(RowBounds(100, 20))
  *     ...
  *   }
  *
  * }}}
  * @tparam Result retult type
  */
abstract class SelectListPage[Result : Manifest] 
  extends Select 
  with SQLFunction1[RowBounds,Seq[Result]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[Result].erasure

  def apply(rowBounds : RowBounds)(implicit s : Session) : Seq[Result]
    = s.selectList[Null,Result](fqi.id, null, rowBounds)

  def handle(rowBounds : RowBounds, callback : ResultContext => Unit)(implicit s : Session) : Unit = {
    s.select(fqi.id, rowBounds, new ResultHandlerDelegator(callback))
  }

}

/** Query for a list of objects with RowBounds and one input parameter.
  *
  * == Details ==
  * This class defines a function: ((Param, RowBounds) => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findByName = new SelectListPageBy[String,Person] {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findByName("John%", RowBounds(100, 20))
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam Result retult type
  */
abstract class SelectListPageBy[Param : Manifest, Result : Manifest] 
  extends Select
  with SQLFunction2[Param, RowBounds, Seq[Result]] {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply(param : Param, rowBounds : RowBounds)(implicit s : Session) : Seq[Result]
    = s.selectList[Param,Result](fqi.id, param, rowBounds)

  def handle(param : Param, rowBounds : RowBounds, callback : ResultContext => Unit)(implicit s : Session) : Unit = {
    s.select(fqi.id, param, rowBounds, new ResultHandlerDelegator(callback))
  }

}

/** Query for a single object.
  *
  * == Details ==
  * This class defines a function: (=> Result)
  *
  * == Sample code ==
  * {{{
  *   val find = new SelectOne[Person] {
  *     def xsql = "SELECT * FROM person WHERE id = 1"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val p = find()
  *     ...
  *   }
  *
  * }}}
  * @tparam Result retult type
  */
abstract class SelectOne[Result : Manifest] 
  extends Select
  with SQLFunction0[Option[Result]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[Result].erasure

  def apply()(implicit s : Session) : Option[Result] = {
    val r = s.selectOne[Result](fqi.id);
    if (r == null) None else Some(r)
  }

}

/** Query for a single object using an input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => Result)
  *
  * == Sample code ==
  * {{{
  *   val find = new SelectOneBy[Int,Person] {
  *     def xsql = "SELECT * FROM person WHERE id = #{id}"
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
abstract class SelectOneBy[Param : Manifest, Result : Manifest] 
  extends Select
  with SQLFunction1[Param, Option[Result]] {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[Result].erasure

  def apply(param : Param)(implicit s : Session) : Option[Result] = {
    val r = s.selectOne[Param,Result](fqi.id, param)
    if (r == null) None else Some(r)
  }

}

/** Query for a Map of objects.
  *
  * == Details ==
  * This class defines a function: (=> Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMap[Long,Person](mapKey="id") {
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
  * @tparam ResultKey map Key type
  * @tparam ResultValue map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMap[ResultKey, ResultValue : Manifest](mapKey : String) 
  extends Select
  with SQLFunction0[Map[ResultKey, ResultValue]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[ResultValue].erasure

  def apply()(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[ResultKey,ResultValue](fqi.id, mapKey)

}

/** Query for a Map of objects using an input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapBy[String,Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById("John%")
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam ResultKey map Key type
  * @tparam ResultValue map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapBy[Param : Manifest, ResultKey, ResultValue : Manifest](mapKey : String) 
  extends Select
  with SQLFunction1[Param, Map[ResultKey, ResultValue]] {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[ResultValue].erasure

  def apply(param : Param)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Param,ResultKey,ResultValue](fqi.id, param, mapKey)

} 

/** Query for a Map of objects with RowBounds.
  *
  * == Details ==
  * This class defines a function: (RowBounds => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapPage[Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById(RowBounds(100,20))
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam ResultKey map Key type
  * @tparam ResultValue map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapPage[ResultKey, ResultValue : Manifest](mapKey : String) 
  extends Select
  with SQLFunction1[RowBounds, Map[ResultKey, ResultValue]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[ResultValue].erasure

  def apply(rowBounds : RowBounds)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Null,ResultKey,ResultValue](fqi.id, null, mapKey, rowBounds)

} 

/** Query for a Map of objects with RowBounds and one input parameter.
  *
  * == Details ==
  * This class defines a function: ((Param, RowBounds) => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapPageBy[String,Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById("John%", RowBounds(100,20))
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam Param input parameter type
  * @tparam ResultKey map Key type
  * @tparam ResultValue map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapPageBy[Param : Manifest, ResultKey, ResultValue : Manifest](mapKey : String) 
  extends Select
  with SQLFunction2[Param, RowBounds, Map[ResultKey, ResultValue]] {

  def parameterTypeClass = manifest[Param].erasure
  def resultTypeClass = manifest[ResultValue].erasure

  def apply(param : Param, rowBounds : RowBounds)(implicit s : Session) : Map[ResultKey, ResultValue]
    = s.selectMap[Param,ResultKey,ResultValue](fqi.id, param, mapKey, rowBounds)

} 
 
