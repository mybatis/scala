package org.mybatis.scala

import org.mybatis.scala.config.Configuration
import org.mybatis.scala.infrastructure.{BlogRepository, UserRepository}

/**
 * Provides [[org.mybatis.scala.session.SessionManager]] instances.
 */
object Database {
  val config = Configuration("mybatis.xml")

  config.addSpace("test") { space =>
    space ++= DatabaseSchema
    space ++= UserRepository
    space ++= BlogRepository
  }

  /**
   * A configuration intended to be used for simple tests.
   */
  lazy val default = config.createPersistenceContext
}

