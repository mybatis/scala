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
package org.mybatis.scala.session

import org.apache.ibatis.session.{TransactionIsolationLevel => TIL}

sealed trait TransactionIsolationLevel {
  val unwrap : TIL
}

object TransactionIsolationLevel {
  val NONE              = new TransactionIsolationLevel { val unwrap = TIL.NONE }
  val READ_COMMITTED    = new TransactionIsolationLevel { val unwrap = TIL.READ_COMMITTED }
  val READ_UNCOMMITTED  = new TransactionIsolationLevel { val unwrap = TIL.READ_UNCOMMITTED }
  val REPEATABLE_READ   = new TransactionIsolationLevel { val unwrap = TIL.REPEATABLE_READ }
  val SERIALIZABLE      = new TransactionIsolationLevel { val unwrap = TIL.SERIALIZABLE }
  val UNDEFINED         = new TransactionIsolationLevel { val unwrap = null }
}
