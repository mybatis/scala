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

package org.mybatis.scala.samples

import org.mybatis.scala.mapping._
import org.mybatis.scala.config._
import org.mybatis.scala.session._

object SelectWithResultMapSample {

  // Simple Group POJO
  class Group {
    var id : Int = _
    var name : String = _
  }

  // Simple ContactInfo POJO
  class ContactInfo {
    var id : Int = _
    var address : String = _
    var phone : String = _
  }

  // Simple Person POJO
  class Person {
    var id : Int = _
    var firstName : String = _
    var lastName : String = _
    var group : Group = _
    var contact : java.util.List[ContactInfo] = _
  }

  // Simple select method
  val findAll = new SelectList[Nothing,Person] {

    // Define the result mapping
    resultMap = new ResultMap[Person] {

      id(property="id", column="id_")
      result(property="firstName", column="first_name_")
      result(property="lastName", column="last_name_")

      association[Group] (property="group", column="group_id_",
        resultMap= new ResultMap[Group] {
          id(property="id", column="group_id_")
          result(property="name", column="group_name_")
        }
      )

      collection[ContactInfo] (property="contact", column="cinfo_id_",
        resultMap= new ResultMap[ContactInfo] {
          id(property="id", column="cinfo_id_")
          result(property="address", column="street_address_")
          result(property="phone", column="phone_number_")
        }
      )

    }

    // Define the actual query
    def xsql =
      <xsql>
        SELECT
          p.id_, p.first_name_, p.last_name_,
          p.group_id_, g.name_ as group_name_,
          c.id_ as cinfo_id_, c.street_address_, c.phone_number_
        FROM
          person p
            LEFT OUTER JOIN people_group g ON p.group_id_ = g.id_
            LEFT OUTER JOIN contact_info c ON c.owner_id_ = p.id_
      </xsql>
  }

  // Load datasource configuration
  val config = Configuration("mybatis.xml")

  // Create a configuration space, add the data access method
  config.addSpace("ns1") { space =>
    space += findAll
  }

  // Build the session manager
  val db = config.createPersistenceContext

  // Do the Magic ...
  def main(args : Array[String]) : Unit = {

    db.readOnly { implicit session =>

      import scala.collection.JavaConversions._

      for (p <- findAll()) {
        println("\nPerson(%d): %s %s is in group: %s".format(p.id, p.firstName, p.lastName, p.group.name))
        for (contact <- p.contact) {
          println("  Address: %s, Phone: %s".format(contact.address, contact.phone))
        }
      }

    }

  }


}