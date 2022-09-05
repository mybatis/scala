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
package org.mybatis.scala.samples.util

import org.mybatis.scala.mapping._
import org.mybatis.scala.session._

object DBSampleData {

  val insertPerson = new Insert[java.util.Map[_,_]] {
    def xsql = 
      <xsql>
        INSERT INTO person(id_, first_name_, last_name_, group_id_)
        VALUES (#{{id}}, #{{firstname}}, #{{lastname}}, #{{group}})
      </xsql>
  }
  
  val insertGroup = new Insert[java.util.Map[_,_]] {
    def xsql = 
      <xsql>
        INSERT INTO people_group(id_, name_)
        VALUES (#{{id}}, #{{name}})
      </xsql>
  }
  
  val insertContactInfo = new Insert[java.util.Map[_,_]] {
    def xsql = 
      <xsql>
        INSERT INTO contact_info (owner_id_, street_address_, phone_number_)
        VALUES (#{{person}}, #{{address}}, #{{phone}})
      </xsql>
  }
  
  def bind = Seq(insertContactInfo, insertGroup, insertPerson)
  
  def populate(implicit s : Session) = {
    
    import scala.jdk.CollectionConverters._
    
    insertGroup(Map("id" -> 1, "name" -> "Customers").asJava)
    insertGroup(Map("id" -> 2, "name" -> "Suppliers").asJava)
    insertGroup(Map("id" -> 3, "name" -> "Employees").asJava)
    
    insertPerson(Map("id" -> 1, "firstname" -> "John", "lastname" -> "Smart", "group" -> 1).asJava)
    insertPerson(Map("id" -> 2, "firstname" -> "Maria", "lastname" -> "Perez", "group" -> 2).asJava)
    insertPerson(Map("id" -> 3, "firstname" -> "Janeth", "lastname" -> "Ros", "group" -> 1).asJava)
    insertPerson(Map("id" -> 4, "firstname" -> "Paul", "lastname" -> "Jobs", "group" -> 3).asJava)
    insertPerson(Map("id" -> 5, "firstname" -> "Bill", "lastname" -> "Rich", "group" -> 1).asJava)
    
    insertContactInfo(Map("person" -> 1, "address" -> "222 Street",         "phone" -> "555-0988998").asJava)
    insertContactInfo(Map("person" -> 2, "address" -> "333 Av",             "phone" -> "554-7464363").asJava)
    insertContactInfo(Map("person" -> 2, "address" -> "1 Rose Ave",         "phone" -> "836-8456463").asJava)
    insertContactInfo(Map("person" -> 3, "address" -> "444 St Rose",        "phone" -> "665-9476558").asJava)
    insertContactInfo(Map("person" -> 4, "address" -> "555 Wall Street",    "phone" -> "666-7474664").asJava)
    insertContactInfo(Map("person" -> 5, "address" -> "666 Mountain View",  "phone" -> "571-9875923").asJava)
    insertContactInfo(Map("person" -> 5, "address" -> "777 Mars",           "phone" -> "587-3984792").asJava)
    
  }
  
}
