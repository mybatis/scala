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

/** Abstract KeyGenerator
  * This trait must be implemented by any KeyGenerator.
  */
trait KeyGenerator {
  var keyProperty : String
  var keyColumn   : String
}

/** JDBC 3 Key Generator implementation.
  * This uses JDBC generatedKeys
  * @param keyColumn Column to be read from the generated keys resultset.
  * @param keyProperty Property to be set with the generated key value.
  */
class JdbcGeneratedKey(keyColumn_ : String, keyProperty_ : String) extends KeyGenerator {
  var keyColumn = keyColumn_
  var keyProperty = keyProperty_
}

/** Factory of JdbcGeneratedKey */
object JdbcGeneratedKey {

  /** Crteates a new JdbcGeneratedKey
    * @param keyColumn Column to be read from the generated keys resultset.
    * @param keyProperty Property to be set with the generated key value.
    */
  def apply(keyColumn : String, keyProperty : String) : JdbcGeneratedKey = new JdbcGeneratedKey(keyColumn, keyProperty)

}

/** Base class to define a native SQL Key generator.
  * @tparam Type result type of the generated key.
  * == Sample code ==
  * {{{
  *   keyGenerator = new SqlGeneratedKey[Long] {
  *     keyProperty = "myId"
  *     def xsql = "SELECT currval('my_sequence')"
  *   }
  * }}}
  */
abstract class SqlGeneratedKey[Type : Manifest] extends KeyGenerator {

  /** Any one of STATEMENT, PREPARED or CALLABLE.
    * This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively.
    * Default: PREPARED.
    */
  var statementType   : StatementType = StatementType.PREPARED

  /** Property to be set. */
  var keyProperty     : String = "id"

  /** Property to be set. */
  var keyColumn       : String = null

  /** If true then this statement will be executed before the main statement. */
  var executeBefore   : Boolean = false

  /** Returns the Class of the generated key. */
  val resultTypeClass = manifest[Type].erasure

  /** Dynamic SQL CODE to be executed in order to obtain/generate the key
    * == Code sample ==
    * {{{
    *   def xsql = "SELECT currval('my_sequence')"
    * }}}
    */
  def xsql : XSQL

}

