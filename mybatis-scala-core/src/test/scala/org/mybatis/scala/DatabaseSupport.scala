package org.mybatis.scala

import org.mybatis.scala.config.Configuration
import org.mybatis.scala.session.{Session, SessionManager}
import org.mybatis.scala.infrastructure.UserRepository

/**
 * This trait provides the feature of using databases in test cases.
 */
trait DatabaseSupport {
  /**
   * Provides [[SessionManager]] instances.
   */
  object Database {
    val config = Configuration("mybatis.xml")

    config.addSpace("test") { space =>
      space ++= DatabaseSchema
      space ++= UserRepository
    }

    /**
     * A configuration intended to be used for simple tests.
     */
    lazy val default = config.createPersistenceContext
  }

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
