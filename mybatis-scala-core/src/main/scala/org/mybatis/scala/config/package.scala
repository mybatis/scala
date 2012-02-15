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

package org.mybatis.scala

/** Provides main configuration classes.
  * == Basic usage ==
  * Usual steps are:
  *  - Load the configuration from an external XML file
  *  - Add one or more configuration spaces with mapped statements
  *  - Create the persistenceContext
  *
  * == Code sample ==
  * {{{
  * val config = Configuration("META-INF/mybatis.xml")
  * config.addSpace("ns") { space =>
  *   space ++= MyDAO
  * }
  * val persistenceContext = config.createPersistenceContext
  * }}}
  * @version \$Revision$
  */
package object config {

  type TransactionFactory = org.apache.ibatis.transaction.TransactionFactory
  type JdbcTransactionFactory = org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
  type ManagedTransactionFactory = org.apache.ibatis.transaction.managed.ManagedTransactionFactory
  
  type PooledDataSource = org.apache.ibatis.datasource.pooled.PooledDataSource
  type UnpooledDataSource = org.apache.ibatis.datasource.unpooled.UnpooledDataSource
  type JndiDataSourceFactory = org.apache.ibatis.datasource.jndi.JndiDataSourceFactory
   
}