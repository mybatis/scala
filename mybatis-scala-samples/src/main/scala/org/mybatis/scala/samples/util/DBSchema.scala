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
import org.mybatis.scala.session.Session

object DBSchema {

  val createPeopleGroupTable = new Perform {
    def xsql =
      <xsql>
        CREATE TABLE people_group (
            id_ INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
            name_ varchar(255),
            primary key (id_)
        )
      </xsql>
  }

  val createPersonTable = new Perform {
    def xsql =
      <xsql>
        CREATE TABLE person (
            id_ INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
            first_name_ varchar(255),
            last_name_ varchar(255),
            group_id_ integer not null,
            primary key (id_),
            foreign key (group_id_) references people_group(id_)
        )
      </xsql>
  }

  val createContactInfoTable = new Perform {
    def xsql =
      <xsql>
        CREATE TABLE contact_info (
            id_ INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
            owner_id_ integer not null,
            street_address_ varchar(255),
            phone_number_ varchar(20),
            primary key (id_),
            foreign key (owner_id_) references person(id_)
        )
      </xsql>
  }

  val bind = Seq(createPeopleGroupTable, createPersonTable, createContactInfoTable)

  def create(implicit s: Session): Unit = {
    createPeopleGroupTable()
    createPersonTable()
    createContactInfoTable()
  }

}
