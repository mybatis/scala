package org.mybatis.scala.domain

/**
 * This class provides information about an user.
 * @param id    the identity to specify a user
 * @param name  the user name
 * @param email the email address
 */
case class User(var id: Int, var name: String, var email: String)
