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

package org.mybatis.scala.config

import org.apache.ibatis.session.{Configuration => MBConfig, SqlSessionFactoryBuilder}
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import java.io.Reader
import org.apache.ibatis.io.Resources
import org.mybatis.scala.session.SessionManager

/** Mybatis Configuration
  * @constructor Creates a new Configuration with a wrapped myBatis Configuration.
  * @param configuration A myBatis Configuration instance.
  * @author Frank D. Martinez M. [mnesarco at gmail.com]
  */
sealed class Configuration(configuration : MBConfig) {

  /** Creates a new space of mapped statements.
    * @param name A Speca name (a.k.a. namespace)
    * @param f A configuration block.
    * @returns This
    */
  def addSpace(name : String)(f : (ConfigurationSpace => Unit)) : this.type = {
    val space = new ConfigurationSpace(configuration, name)
    f(space)
    this
  }

  /** Builds a Session Manager */
  def createPersistenceContext = {
    val builder = new SqlSessionFactoryBuilder
    new SessionManager(builder.build(configuration))
  }

}

/** A factory of [[org.mybatis.scala.config.Configuration]] instances.
  * @author Frank D. Martinez M. [mnesarco at gmail.com]
  */
object Configuration {

  /** Creates a Configuration built from a reader.
    * @param reader Reader over a myBatis main configuration XML
    */
  def apply(reader : Reader) : Configuration = {
    val builder = new XMLConfigBuilder(reader)
    new Configuration(builder.parse)
  }

  /** Creates a Configuration built from a classpath resource.
    * @param path Classpath resource with main configuration XML
    */
  def apply(path : String) : Configuration = {
    apply(Resources.getResourceAsReader(path))
  }

}
