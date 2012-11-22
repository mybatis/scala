/*
 * Copyright 2012 MyBatis.org.
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

import org.mybatis.scala.session.Session

/** A mapped SQL UPDATE statement.
  * Basically this defines a function: (=> Int)
  */
abstract class Perform 
  extends Statement
  with SQLFunction0[Int] {

  def parameterTypeClass = classOf[Nothing]
  
  /** Exceutes the SQL Statement
    * @param s Implicit Session
    * @return number of affected rows
    */
  def apply()(implicit s : Session) : Int =
    execute { s.update(fqi.id) }
  
}
