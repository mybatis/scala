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

package org.mybatis.scala.config

import org.apache.ibatis.session.{Configuration => MBConfig, SqlSessionFactoryBuilder}
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import java.io.Reader
import org.apache.ibatis.io.Resources
import org.mybatis.scala.session.SessionManager
import java.util.Properties
import org.mybatis.scala.mapping.Statement
import org.mybatis.scala.mapping.T
import org.mybatis.scala.cache._
import org.apache.ibatis.`type`.TypeHandler

/** Mybatis Configuration
  * @constructor Creates a new Configuration with a wrapped myBatis Configuration.
  * @param configuration A myBatis Configuration instance.
  */
sealed class Configuration(configuration : MBConfig) {
  
  if (configuration.getObjectFactory().getClass == classOf[org.apache.ibatis.reflection.factory.DefaultObjectFactory]) {
    configuration.setObjectFactory(new ObjectFactory())
  }
  
  if (configuration.getObjectWrapperFactory.getClass == classOf[org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory]) {
    configuration.setObjectWrapperFactory(new ObjectWrapperFactory())
  }

  /** Register Option[_] TypeHandlers */
  registerAdditionalTypeHandlers()

  lazy val defaultSpace = new ConfigurationSpace(configuration, "_DEFAULT_")

  /** Creates a new space of mapped statements.
    * @param name A Speca name (a.k.a. namespace)
    * @param f A configuration block.
    */
  def addSpace(name : String)(f : (ConfigurationSpace => Unit)) : this.type = {
    val space = new ConfigurationSpace(configuration, name)
    f(space)
    this
  }

  /** Adds a statement to the default space */
  def += (s : Statement) = defaultSpace += s

  /** Adds a sequence of statements to the default space */
  def ++=(ss : Seq[Statement]) = defaultSpace ++= ss

  /** Adds a mapper to the space */
  def ++=(mapper : { def bind : Seq[Statement] }) = defaultSpace ++= mapper

  /** Adds cache support to this space.
    * @param impl Cache implementation class
    * @param eviction cache eviction policy (LRU,FIFO,WEAK,SOFT)
    * @param flushInterval any positive integer in milliseconds.
    *        The default is not set, thus no flush interval is used and the cache is only flushed by calls to statements.
    * @param size max number of objects that can live in the cache. Default is 1024
    * @param readWrite A read-only cache will return the same instance of the cached object to all callers.
    *        Thus such objects should not be modified.  This offers a significant performance advantage though.
    *        A read-write cache will return a copy (via serialization) of the cached object,
    *        this is slower, but safer, and thus the default is true.
    * @param props implementation specific properties.
    */
  def cache(
    impl : T[_ <: Cache] = DefaultCache,
    eviction : T[_ <: Cache] = Eviction.LRU,
    flushInterval : Long = -1,
    size : Int = -1,
    readWrite : Boolean = true,
    props : Properties = null) = 
      defaultSpace.cache(impl, eviction, flushInterval, size, readWrite, props)

  /** Reference to an external Cache */
  def cacheRef(that : ConfigurationSpace) = defaultSpace.cacheRef(that)
  
  /** Builds a Session Manager */
  def createPersistenceContext = {
    val builder = new SqlSessionFactoryBuilder
    new SessionManager(builder.build(configuration))
  }

  def registerOptionTypeHandler[T](h : TypeHandler[T], jdbcTypes : Seq[org.apache.ibatis.`type`.JdbcType]) = {
    import org.mybatis.scala.mapping.OptionTypeHandler
    val registry = configuration.getTypeHandlerRegistry
    val oth = new OptionTypeHandler[T](h)
    val cls = classOf[Option[_]]
    for (jdbcType <- jdbcTypes) {
      registry.register(cls, jdbcType, oth)
    }
  }

  private def registerAdditionalTypeHandlers() = {
    import org.apache.ibatis.`type`._
    import org.apache.ibatis.`type`.JdbcType._
    registerOptionTypeHandler(new BooleanTypeHandler(), Seq(BOOLEAN, BIT))
    registerOptionTypeHandler(new ByteTypeHandler(), Seq(TINYINT))
    registerOptionTypeHandler(new ShortTypeHandler(), Seq(SMALLINT))
    registerOptionTypeHandler(new IntegerTypeHandler(), Seq(INTEGER))
    registerOptionTypeHandler(new FloatTypeHandler(), Seq(FLOAT))
    registerOptionTypeHandler(new DoubleTypeHandler(), Seq(DOUBLE))
    registerOptionTypeHandler(new LongTypeHandler(), Seq(BIGINT))
    registerOptionTypeHandler(new StringTypeHandler(), Seq(VARCHAR, CHAR))
    registerOptionTypeHandler(new ClobTypeHandler(), Seq(CLOB, LONGVARCHAR))
    registerOptionTypeHandler(new NStringTypeHandler(), Seq(NVARCHAR, NCHAR))
    registerOptionTypeHandler(new NClobTypeHandler(), Seq(NCLOB))
    registerOptionTypeHandler(new BigDecimalTypeHandler(), Seq(REAL, DECIMAL, NUMERIC))
    registerOptionTypeHandler(new BlobTypeHandler(), Seq(BLOB, LONGVARBINARY))
    registerOptionTypeHandler(new UnknownTypeHandler(configuration.getTypeHandlerRegistry), Seq(OTHER))
    registerOptionTypeHandler(new DateOnlyTypeHandler(), Seq(DATE))
    registerOptionTypeHandler(new TimeOnlyTypeHandler(), Seq(TIME))
    registerOptionTypeHandler(new DateTypeHandler(), Seq(TIMESTAMP))    
  }

}

/** A factory of [[org.mybatis.scala.config.Configuration]] instances. */
object Configuration {

  /** Creates a Configuration built from a reader.
    * @param reader Reader over a myBatis main configuration XML
    */
  def apply(reader : Reader) : Configuration = {
    val builder = new XMLConfigBuilder(reader)
    new Configuration(builder.parse)
  }

  /** Creates a Configuration built from a reader.
    * @param reader Reader over a myBatis main configuration XML
    * @param env Environment name
    */
  def apply(reader : Reader, env : String) : Configuration = {
    val builder = new XMLConfigBuilder(reader, env)
    new Configuration(builder.parse)
  }

  /** Creates a Configuration built from a reader.
    * @param reader Reader over a myBatis main configuration XML
    * @param env Environment name
    * @param properties Properties
    */
  def apply(reader : Reader, env : String, properties : Properties) : Configuration = {
    val builder = new XMLConfigBuilder(reader, env, properties)
    new Configuration(builder.parse)
  }

  /** Creates a Configuration built from a classpath resource.
    * @param path Classpath resource with main configuration XML
    */
  def apply(path : String) : Configuration = {
    apply(Resources.getResourceAsReader(path))
  }

  /** Creates a Configuration built from a classpath resource.
    * @param path Classpath resource with main configuration XML
    * @param env Environment name
    */
  def apply(path : String, env : String) : Configuration = {
    apply(Resources.getResourceAsReader(path), env)
  }

  /** Creates a Configuration built from a classpath resource.
    * @param path Classpath resource with main configuration XML
    * @param env Environment name
    * @param properties Properties
    */
  def apply(path : String, env : String, properties : Properties) : Configuration = {
    apply(Resources.getResourceAsReader(path), env, properties)
  }

  /** Creates a Configuration built from an environment
    * @param env Environment
    */
  def apply(env : Environment) : Configuration = {
    new Configuration(new MBConfig(env.unwrap))
  }

}
