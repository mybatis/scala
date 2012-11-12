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

package org.mybatis.scala.samples.select

import org.mybatis.scala.mapping._
import org.mybatis.scala.config._
import org.mybatis.scala.session._
import org.mybatis.scala.samples.util._

// Model beans (Immutable case class) ==========================================

case class CPerson(id : Int, firstName : String, lastName : String)

// Data access layer ===========================================================

object CDB {

  // Simple select function
  val findAll = new SelectListBy[String,CPerson] {
    
    // Constructor Mapping (Warning: Order is important)
    resultMap = new ResultMap[CPerson] {
      idArg(column="id_", javaType=T[Int])
      arg(column="first_name_", javaType=T[String])
      arg(column="last_name_", javaType=T[String])
    }
    
    def xsql =
      """
        SELECT
          id_, first_name_, last_name_
        FROM
          person
        WHERE
          first_name_ LIKE #{name}
      """
  }

  // Datasource configuration
  val config = Configuration(
    Environment(
      "default", 
      new JdbcTransactionFactory(), 
      new PooledDataSource(
        "org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:scala",
        "sa",
        ""
      )
    )
  )

  // Add the data access method to the default namespace
  config += findAll
  config ++= DBSchema
  config ++= DBSampleData

  // Build the session manager
  lazy val context = config.createPersistenceContext
  
}

// Application code ============================================================

object SelectImmutableSample {

  // Do the Magic ...
  def main(args : Array[String]) : Unit = {
    CDB.context.transaction { implicit session =>

      DBSchema.create
      DBSampleData.populate

      CDB.findAll("%a%").foreach { 
        case CPerson(id, firstName, lastName) =>
        	println( "Person(%d): %s %s".format(id, firstName, lastName) )
      }
      
    }
  }

}
