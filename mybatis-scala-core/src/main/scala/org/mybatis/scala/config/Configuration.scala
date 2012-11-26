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

import org.apache.ibatis.session.{ Configuration => MBConfig, SqlSessionFactoryBuilder }
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import java.io.Reader
import org.apache.ibatis.io.Resources
import org.mybatis.scala.session.SessionManager
import java.util.Properties
import org.mybatis.scala.mapping.Statement
import org.mybatis.scala.mapping.T
import org.mybatis.scala.cache._
import org.apache.ibatis.`type`.TypeHandler

/**
 * Mybatis Configuration
 * @constructor Creates a new Configuration with a wrapped myBatis Configuration.
 * @param configuration A myBatis Configuration instance.
 */
sealed class Configuration(private val configuration: MBConfig) {

  if (configuration.getObjectFactory().getClass == classOf[org.apache.ibatis.reflection.factory.DefaultObjectFactory]) {
    configuration.setObjectFactory(new DefaultObjectFactory())
  }

  if (configuration.getObjectWrapperFactory.getClass == classOf[org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory]) {
    configuration.setObjectWrapperFactory(new DefaultObjectWrapperFactory())
  }

  registerCommonOptionTypeHandlers

  lazy val defaultSpace = new ConfigurationSpace(configuration, "_DEFAULT_")

  /**
   * Creates a new space of mapped statements.
   * @param name A Speca name (a.k.a. namespace)
   * @param f A configuration block.
   */
  def addSpace(name: String)(f: (ConfigurationSpace => Unit)): this.type = {
    val space = new ConfigurationSpace(configuration, name)
    f(space)
    this
  }

  /** Adds a statement to the default space */
  def +=(s: Statement) = defaultSpace += s

  /** Adds a sequence of statements to the default space */
  def ++=(ss: Seq[Statement]) = defaultSpace ++= ss

  /** Adds a mapper to the space */
  def ++=(mapper: { def bind: Seq[Statement] }) = defaultSpace ++= mapper

  /**
   * Adds cache support to this space.
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
    impl: T[_ <: Cache] = DefaultCache,
    eviction: T[_ <: Cache] = Eviction.LRU,
    flushInterval: Long = -1,
    size: Int = -1,
    readWrite: Boolean = true,
    props: Properties = null) =
    defaultSpace.cache(impl, eviction, flushInterval, size, readWrite, props)

  /** Reference to an external Cache */
  def cacheRef(that: ConfigurationSpace) = defaultSpace.cacheRef(that)

  /** Builds a Session Manager */
  def createPersistenceContext = {
    val builder = new SqlSessionFactoryBuilder
    new SessionManager(builder.build(configuration))
  }

  private def registerOptionTypeHandler[T <: Option[_]](h: TypeHandler[T], jdbcTypes: Seq[org.apache.ibatis.`type`.JdbcType]) = {
    import org.mybatis.scala.mapping.OptionTypeHandler
    val registry = configuration.getTypeHandlerRegistry
    val cls = classOf[Option[_]]
    for (jdbcType <- jdbcTypes) {
      registry.register(cls, jdbcType, h)
    }
  }

  private def registerCommonOptionTypeHandlers = {
    import org.mybatis.scala.mapping.OptionTypeHandler
    import org.mybatis.scala.mapping.TypeHandlers._
    import org.apache.ibatis.`type`._
    import org.apache.ibatis.`type`.JdbcType._
    registerOptionTypeHandler(new OptBooleanTypeHandler(), Seq(BOOLEAN, BIT))
    registerOptionTypeHandler(new OptByteTypeHandler(), Seq(TINYINT))
    registerOptionTypeHandler(new OptShortTypeHandler(), Seq(SMALLINT))
    registerOptionTypeHandler(new OptIntegerTypeHandler(), Seq(INTEGER))
    registerOptionTypeHandler(new OptFloatTypeHandler(), Seq(FLOAT))
    registerOptionTypeHandler(new OptDoubleTypeHandler(), Seq(DOUBLE))
    registerOptionTypeHandler(new OptLongTypeHandler(), Seq(BIGINT))
    registerOptionTypeHandler(new OptStringTypeHandler(), Seq(VARCHAR, CHAR))
    registerOptionTypeHandler(new OptClobTypeHandler(), Seq(CLOB, LONGVARCHAR))
    registerOptionTypeHandler(new OptNStringTypeHandler(), Seq(NVARCHAR, NCHAR))
    registerOptionTypeHandler(new OptNClobTypeHandler(), Seq(NCLOB))
    registerOptionTypeHandler(new OptBigDecimalTypeHandler(), Seq(REAL, DECIMAL, NUMERIC))
    registerOptionTypeHandler(new OptBlobTypeHandler(), Seq(BLOB, LONGVARBINARY))
    registerOptionTypeHandler(new OptDateTypeHandler(), Seq(DATE))
    registerOptionTypeHandler(new OptTimeTypeHandler(), Seq(TIME))
    registerOptionTypeHandler(new OptTimestampTypeHandler(), Seq(TIMESTAMP))
    registerOptionTypeHandler(new OptionTypeHandler(new UnknownTypeHandler(configuration.getTypeHandlerRegistry)), Seq(OTHER))
  }

}

/** A factory of [[org.mybatis.scala.config.Configuration]] instances. */
object Configuration {

  /**
   * Creates a Configuration built from a reader.
   * @param reader Reader over a myBatis main configuration XML
   */
  def apply(reader: Reader): Configuration = {
    val builder = new XMLConfigBuilder(reader)
    new Configuration(builder.parse)
  }

  /**
   * Creates a Configuration built from a reader.
   * @param reader Reader over a myBatis main configuration XML
   * @param env Environment name
   */
  def apply(reader: Reader, env: String): Configuration = {
    val builder = new XMLConfigBuilder(reader, env)
    new Configuration(builder.parse)
  }

  /**
   * Creates a Configuration built from a reader.
   * @param reader Reader over a myBatis main configuration XML
   * @param env Environment name
   * @param properties Properties
   */
  def apply(reader: Reader, env: String, properties: Properties): Configuration = {
    val builder = new XMLConfigBuilder(reader, env, properties)
    new Configuration(builder.parse)
  }

  /**
   * Creates a Configuration built from a classpath resource.
   * @param path Classpath resource with main configuration XML
   */
  def apply(path: String): Configuration = {
    apply(Resources.getResourceAsReader(path))
  }

  /**
   * Creates a Configuration built from a classpath resource.
   * @param path Classpath resource with main configuration XML
   * @param env Environment name
   */
  def apply(path: String, env: String): Configuration = {
    apply(Resources.getResourceAsReader(path), env)
  }

  /**
   * Creates a Configuration built from a classpath resource.
   * @param path Classpath resource with main configuration XML
   * @param env Environment name
   * @param properties Properties
   */
  def apply(path: String, env: String, properties: Properties): Configuration = {
    apply(Resources.getResourceAsReader(path), env, properties)
  }

  /**
   * Creates a Configuration built from an environment
   * @param env Environment
   */
  def apply(env: Environment): Configuration = {
    new Configuration(new MBConfig(env.unwrap))
  }

  /**
   * Creates a Configuration built from a custom builder
   * @param builder Builder => Unit
   */
  def apply(builder: Builder): Configuration = builder.build

  /** Configuration builder */
  class Builder {

    import org.mybatis.scala.mapping.JdbcType
    import org.apache.ibatis.plugin.Interceptor
    import scala.collection.mutable.ArrayBuffer
    import org.mybatis.scala.session.ExecutorType
    import scala.collection.JavaConversions._
    import org.apache.ibatis.mapping.Environment

    /** Reference to self. Support for operational notation. */
    protected val >> = this

    /** Mutable hidden state, discarded after construction */
    private val pre = new ArrayBuffer[ConfigElem[MBConfig]]()

    /** Mutable hidden state, discarded after construction */
    private val pos = new ArrayBuffer[ConfigElem[Configuration]]()

    /** Configuration Element */
    private abstract class ConfigElem[T] {
      val index: Int
      def set(config: T): Unit
    }

    /** Ordered deferred setter */
    private def set[A](i: Int, e: ArrayBuffer[ConfigElem[A]])(f: A => Unit) {
      e += new ConfigElem[A] {
        val index = i
        def set(c: A) = f(c)
      }
    }

    /** Builds the configuration object */
    private[Configuration] def build(): Configuration = {
      val preConfig = new MBConfig()
      pre.sortWith((a, b) => a.index < b.index).foreach(_.set(preConfig))
      val config = new Configuration(preConfig)
      pos.sortWith((a, b) => a.index < b.index).foreach(_.set(config))
      config
    }

    // Pre ====================================================================
    
    def properties(props: (String, String)*) =
      set(0, pre) { _.getVariables().putAll(Map(props: _*)) }

    def properties(props: Properties) =
      set(1, pre) { _.getVariables.putAll(props) }

    def properties(resource: String) =
      set(2, pre) { _.getVariables.putAll(Resources.getResourceAsProperties(resource)) }

    def propertiesFromUrl(url: String) =
      set(3, pre) { _.getVariables.putAll(Resources.getUrlAsProperties(url)) }

    def plugin(plugin: Interceptor) =
      set(4, pre) { _.addInterceptor(plugin) }

    def objectFactory(factory: ObjectFactory) =
      set(5, pre) { _.setObjectFactory(factory) }

    def objectWrapperFactory(factory: ObjectWrapperFactory) =
      set(6, pre) { _.setObjectWrapperFactory(factory) }

    // Settings ===============

    def autoMappingBehavior(behavior: AutoMappingBehavior) =
      set(8, pre) { _.setAutoMappingBehavior(behavior.unwrap) }

    def cacheSupport(enabled: Boolean) =
      set(9, pre) { _.setCacheEnabled(enabled) }

    def lazyLoadingSupport(enabled: Boolean) =
      set(10, pre) { _.setLazyLoadingEnabled(enabled) }

    def aggressiveLazyLoading(enabled: Boolean) = 
      set(11, pre) { _.setAggressiveLazyLoading(enabled) }
    
    def multipleResultSetsSupport(enabled: Boolean) = 
      set(12, pre) { _.setMultipleResultSetsEnabled(enabled) }
    
    def useColumnLabel(enabled: Boolean) = 
      set(13, pre) { _.setUseColumnLabel(enabled) }
    
    def useGeneratedKeys(enabled: Boolean) = 
      set(14, pre) { _.setUseGeneratedKeys(enabled) }
    
    def defaultExecutorType(executorType: ExecutorType) = 
      set(15, pre) { _.setDefaultExecutorType(executorType.unwrap) }
    
    def defaultStatementTimeout(timeout: Int) = 
      set(16, pre) { _.setDefaultStatementTimeout(timeout) }
    
    def mapUnderscoreToCamelCase(enabled: Boolean) = 
      set(17, pre) { _.setMapUnderscoreToCamelCase(enabled) }
    
    def safeRowBoundsSupport(enabled: Boolean) = 
      set(18, pre) { _.setSafeRowBoundsEnabled(enabled) }
    
    def localCacheScope(localCacheScope: LocalCacheScope) = 
      set(19, pre) { _.setLocalCacheScope(localCacheScope.unwrap) }
    
    def jdbcTypeForNull(jdbcType: JdbcType) = 
      set(20, pre) { _.setJdbcTypeForNull(jdbcType.unwrap) }
    
    def lazyLoadTriggerMethods(names: Set[String]) = 
      set(21, pre) { _.setLazyLoadTriggerMethods(names) }
    
    def environment(id: String, transactionFactory: TransactionFactory, dataSource: javax.sql.DataSource) =
      set(24, pre) { _.setEnvironment(new Environment(id, transactionFactory, dataSource)) }

    def databaseIdProvider(provider: DatabaseIdProvider) =
      set(25, pre) { c => c.setDatabaseId(provider.getDatabaseId(c.getEnvironment.getDataSource)) }

    def typeHandler(jdbcType: JdbcType, handler: (T[_], TypeHandler[_])) =
      set(26, pre) { _.getTypeHandlerRegistry.register(handler._1.raw, jdbcType.unwrap, handler._2) }

    def typeHandler(handler: (T[_], TypeHandler[_])) = 
      set(26, pre) { _.getTypeHandlerRegistry.register(handler._1.raw, handler._2) }
    
    def typeHandler(handler: TypeHandler[_]) = 
      set(26, pre) { _.getTypeHandlerRegistry.register(handler) }

    // Pos ===========================================================

    def namespace(name: String)(f: (ConfigurationSpace => Unit)) =
      set(0, pos) { c => f(new ConfigurationSpace(c.configuration, name)) }

    def statements(s: Statement*) = 
      set(1, pos) { _ ++= s }
    
    def mappers(mappers: { def bind: Seq[Statement] }*) = 
      set(1, pos) { c => mappers.foreach(c ++= _) }
    
    def cacheRef(that: ConfigurationSpace) = 
      set(2, pos) { _.cacheRef(that) }
    
    def cache(
      impl: T[_ <: Cache] = DefaultCache,
      eviction: T[_ <: Cache] = Eviction.LRU,
      flushInterval: Long = -1,
      size: Int = -1,
      readWrite: Boolean = true,
      props: Properties = null) = 
        set(2, pos) { _.cache(impl, eviction, flushInterval, size, readWrite, props) }

    // PENDING FOR mybatis 3.1.1+ ==================================================
    
    // TODO (3.1.1) def proxyFactory(factory: ProxyFactory) = set( 7, pre) { _.setProxyFactory(factory) }
    // TODO (3.1.1) def safeResultHandlerSupport(enabled : Boolean) = set(22, pre) { _.setSafeResultHandlerEnabled(enabled) }
    // TODO (3.1.1) def defaultScriptingLanguage(driver : T[_]) = set(23, pre) { _.setDefaultScriptingLanguage(driver) }

  }

}
