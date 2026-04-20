/*
 *    Copyright 2011-2026 the original author or authors.
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
package org.mybatis.scala.config

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource
import org.apache.ibatis.mapping.DatabaseIdProvider
import org.apache.ibatis.plugin.{Interceptor, Invocation, Plugin}
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.apache.ibatis.`type`.{IntegerTypeHandler, JdbcType => MBJdbcType, LongTypeHandler, StringTypeHandler}
import org.mybatis.scala.DatabaseSchema
import org.mybatis.scala.mapping.{JdbcType, T}
import org.mybatis.scala.session.ExecutorType
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Files
import java.util.Properties

class ConfigurationBuilderSpec extends AnyFunSpec with Matchers {

  private val testDataSource = new UnpooledDataSource(
    "org.hsqldb.jdbcDriver",
    "jdbc:hsqldb:mem:scala-builder",
    "sa",
    ""
  )

  describe("Configuration object factories") {
    it("should support resource and reader based apply overloads") {
      val c1 = Configuration("mybatis.xml")
      val c2 = Configuration("mybatis.xml", "standalone")
      val props = new Properties()
      props.setProperty("k", "v")
      val c3 = Configuration("mybatis.xml", "standalone", props)

      c1 should not be null
      c2 should not be null
      c3 should not be null
    }

    it("should support environment-based apply overload") {
      val env = Environment("standalone", new JdbcTransactionFactory(), testDataSource)
      val c = Configuration(env)

      c should not be null
    }
  }

  describe("Configuration.Builder") {
    it("should build a configuration with pre and post build options") {
      val tempProps = Files.createTempFile("mybatis-scala-builder", ".properties")
      Files.writeString(tempProps, "k1=v1\n")

      val plugin = new Interceptor {
        override def intercept(invocation: Invocation): AnyRef = invocation.proceed()
        override def plugin(target: AnyRef): AnyRef = Plugin.wrap(target, this)
        override def setProperties(properties: Properties): Unit = ()
      }
      val dbIdProvider = new DatabaseIdProvider {
        override def getDatabaseId(dataSource: javax.sql.DataSource): String = "hsqldb"
        override def setProperties(p: Properties): Unit = ()
      }

      val builder = new Configuration.Builder
      builder.properties("a" -> "1")
      builder.properties({
        val p = new Properties()
        p.setProperty("b", "2")
        p
      })
      builder.properties("builder-test.properties")
      builder.propertiesFromUrl(tempProps.toUri.toString)
      builder.plugin(plugin)
      builder.objectFactory(new DefaultObjectFactory())
      builder.objectWrapperFactory(new DefaultObjectWrapperFactory())
      builder.cacheSupport(enabled = true)
      builder.lazyLoadingSupport(enabled = true)
      builder.aggressiveLazyLoading(enabled = false)
      builder.multipleResultSetsSupport(enabled = true)
      builder.useColumnLabel(enabled = true)
      builder.useGeneratedKeys(enabled = true)
      builder.defaultExecutorType(ExecutorType.REUSE)
      builder.defaultStatementTimeout(10)
      builder.mapUnderscoreToCamelCase(enabled = true)
      builder.safeRowBoundsSupport(enabled = true)
      builder.jdbcTypeForNull(JdbcType.VARCHAR)
      builder.lazyLoadTriggerMethods(Set("equals", "hashCode"))
      builder.environment("standalone", new JdbcTransactionFactory(), testDataSource)
      builder.databaseIdProvider(dbIdProvider)
      builder.typeHandler(JdbcType.VARCHAR, (T[String], new StringTypeHandler()))
      builder.typeHandler((T[Int], new IntegerTypeHandler()))
      builder.typeHandler(new LongTypeHandler())
      builder.namespace("ns1")(_.cache())
      builder.statements(DatabaseSchema.createUserTable)
      builder.mappers(DatabaseSchema)
      builder.cache()

      val config = Configuration(builder)
      config should not be null
      config.createPersistenceContext should not be null
    }
  }
}
