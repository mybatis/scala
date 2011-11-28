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

package org.mybatis.scala

import org.apache.ibatis.`type`.{TypeHandler => MBTypeHandler}
import scala.xml.Node

/** Statement mapping classes */
package object mapping {

  /** Alias of org.apache.ibatis.type.TypeHandler */
  type TypeHandler[T] = MBTypeHandler[T]

  /** Alias of scala.xml.Node */
  type XSQL = Node

  /** Implicit conversion from String to XSQL */
  implicit def string_to_xsql( s : String ) : XSQL = new scala.xml.Text(s)

}