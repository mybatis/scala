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

package org.mybatis.scala

import org.apache.ibatis.`type`.{TypeHandler => MBTypeHandler}
import scala.xml.Node

/** Statement and result mapping classes.
  * == Code sample ==
  * Sample database
  * {{{
  *
  * CREATE TABLE people_group (
  *    id_ serial,
  *    name_ varchar(255),
  *    primary key (id_)
  * );
  *
  * CREATE TABLE person (
  *    id_ serial,
  *    first_name_ varchar(255),
  *    last_name_ varchar(255),
  *    group_id_ integer not null,
  *    primary key (id_),
  *    foreign key (group_id_) references people_group(id_)
  * );
  *
  * CREATE TABLE contact_info (
  *    id_ serial,
  *    owner_id_ integer not null,
  *    street_address_ varchar(255),
  *    phone_number_ varchar(20),
  *    primary key (id_),
  *    foreign key (owner_id_) references person(id_)
  * );
  *
  * }}}
  *
  * Sample Model
  * {{{
  *
  *   // Simple Group POJO
  *   class Group {
  *     var id : Int = _
  *     var name : String = _
  *   }
  *
  *   // Simple ContactInfo POJO
  *   class ContactInfo {
  *     var id : Int = _
  *     var address : String = _
  *     var phone : String = _
  *   }
  *
  *   // Simple Person POJO
  *   class Person {
  *     var id : Int = _
  *     var firstName : String = _
  *     var lastName : String = _
  *     var group : Group = _
  *     var contact : java.util.List[ContactInfo] = _
  *   }
  *
  * }}}
  *
  * Sample Mapping
  * {{{
  *
  *    // Define the result mapping
  *    resultMap = new ResultMap[Person] {
  *
  *      id(property="id", column="id_")
  *      result(property="firstName", column="first_name_")
  *      result(property="lastName", column="last_name_")
  *
  *      association[Group] (property="group", column="group_id_",
  *        resultMap= new ResultMap[Group] {
  *          id(property="id", column="group_id_")
  *          result(property="name", column="group_name_")
  *        }
  *      )
  *
  *      collection[ContactInfo] (property="contact", column="cinfo_id_",
  *        resultMap= new ResultMap[ContactInfo] {
  *          id(property="id", column="cinfo_id_")
  *          result(property="address", column="street_address_")
  *          result(property="phone", column="phone_number_")
  *        }
  *      )
  *    }
  * }}}
  */
package object mapping {

  /** Alias of [[org.apache.ibatis.type.TypeHandler]] */
  type TypeHandler[T] = MBTypeHandler[T]

  /** Alias of [[scala.xml.Node]] */
  type XSQL = Node

  /** Implicit conversion from String to XSQL */
  implicit def string_to_xsql( s : String ) : XSQL = <xsql>{s}</xsql>

}