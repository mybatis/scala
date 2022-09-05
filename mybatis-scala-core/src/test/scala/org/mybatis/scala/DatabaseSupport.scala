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
package org.mybatis.scala

import org.mybatis.scala.config.Configuration
import org.mybatis.scala.session.{Session, SessionManager}
import org.mybatis.scala.infrastructure.{BlogRepository, UserRepository}

/**
 * This trait provides the feature of using databases in test cases.
 */
trait DatabaseSupport {
  /**
   * Executes a callback function provided by a argument of this function within a read-only database transaction.
   * @param block the callback function to be executed within a database transaction.
   */
  def withReadOnly(db: SessionManager)(block: Session => Unit): Unit = {
    db.readOnly { implicit session =>
      DatabaseSchema.prepare
      block(session)
    }
  }
}
