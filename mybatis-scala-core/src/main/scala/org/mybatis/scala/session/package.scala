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

import org.apache.ibatis.session.{ResultHandler => MBResultHandler}
import org.apache.ibatis.session.{RowBounds => MBRowBounds}

/** Session Management classes.
  * Provides classes needed to execute the mapped statements.
  * == Basic usage ==
  * Usual steps are:
  *  - Obtain a SessionManager instance from a Configuration
  *  - Call any of the lifecycle methods of the SessionManager passing the code to be executed.
  * == Code sample ==
  * {{{
  * val db = Config.persistenceContext
  * db.transaction { implicit session =>
  *   MyDAO.insert(...)
  *   MyDAO.update(...)
  *   // etc...
  * }
  * }}}
  * @version \$Revision$
  */
package object session {

  /** Alias of [[org.apache.ibatis.session.ResultHandler]] */
  type ResultHandler = MBResultHandler
  
  /** Alias of [[org.apache.ibatis.executor.BatchResult]] */
  type BatchResult = org.apache.ibatis.executor.BatchResult
  
}