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

import org.apache.ibatis.mapping.{ResultSetType => MBResultSetType}

/** Wrapper of org.apache.ibatis.mapping.ResultSetType */
sealed trait ResultSetType {
  val unwrap : MBResultSetType
}

/** Wrapper of org.apache.ibatis.mapping.ResultSetType values */
object ResultSetType {
  val FORWARD_ONLY = new ResultSetType { val unwrap = MBResultSetType.FORWARD_ONLY }
  val SCROLL_INSENSITIVE = new ResultSetType { val unwrap = MBResultSetType.SCROLL_INSENSITIVE }
  val SCROLL_SENSITIVE = new ResultSetType { val unwrap = MBResultSetType.SCROLL_SENSITIVE }
}