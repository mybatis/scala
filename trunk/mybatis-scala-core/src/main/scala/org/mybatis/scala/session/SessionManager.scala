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

package org.mybatis.scala.session

import org.apache.ibatis.session._

/** Session lifecycle manager.
  * Manages the lifecycle of the Session
  * == Usage ==
  *  - Rollback only
  *    {{{
  *    sessionManager.readOnly { implicit session =>
  *       // Your code ...
  *       // Always rollback at the end automatically.
  *    }
  *    }}}
  *  - Direct transaction
  *    {{{
  *    sessionManager.transaction { implicit session =>
  *       // Your code ...
  *       // Always commit at the end if no exceptions are thrown, else rollback.
  *    }
  *    }}}
  *  - External transaction
  *    {{{
  *    sessionManager.managed { implicit session =>
  *       // Your code ...
  *       // Never commit or rollback automatically.
  *       // The transaction can be managed externally or manually.
  *    }
  *    }}}
  *
  * @version \$Revision$
  */
sealed class SessionManager(factory : SqlSessionFactory) {

  type Callback[T] = (Session) => T
  type OpenSessionHook = (SqlSessionFactory) => SqlSession
  type CloseSessionHook = (SqlSession) => Unit

  private var openSession : OpenSessionHook = {(f : SqlSessionFactory) => f.openSession}
  private var closeSession : CloseSessionHook = {(s : SqlSession) => s.close}

  def openSessionHook(hook : OpenSessionHook) = {
    openSession = hook
  }

  def closeSessionHook(hook : CloseSessionHook) = {
    closeSession = hook
  }

  /** Executes the callback within a new session and rollback at the end. */
  def readOnly[T](callback : Callback[T]) : T = {
    val sqlSession = openSession(factory)
    try {
      val ret = callback(new Session(sqlSession))
      sqlSession.rollback
      ret
    }
    finally {
      closeSession(sqlSession)
    }
  }

  /** Executes the callback within a new transaction and commit at the end, automatically calls rollback if any exception. */
  def transaction[T](callback : Callback[T]) : T = {
    val sqlSession = openSession(factory)
    try {
      val t = callback(new Session(sqlSession))
      sqlSession.commit
      t
    }
    catch {
      case e =>
        sqlSession.rollback
        throw e
    }
    finally {
      closeSession(sqlSession)
    }
  }

  /** Executes the callback within a new session. Does not call any transaction method. */
  def managed[T](callback : Callback[T]) : T = {
    val sqlSession = openSession(factory)
    try {
      callback(new Session(sqlSession))
    }
    finally {
      closeSession(sqlSession)
    }
  }

}
