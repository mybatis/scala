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
