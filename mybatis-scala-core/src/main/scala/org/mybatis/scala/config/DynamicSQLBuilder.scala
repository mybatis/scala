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

import org.apache.ibatis.session.{Configuration => MBConfig}
import java.util.{List, ArrayList}
import org.apache.ibatis.builder.xml.dynamic._
import org.apache.ibatis.mapping.SqlSource
import scala.xml._

/** Builder of Dynamic SQL Trees. */
private[scala] class DynamicSQLBuilder(val configuration : MBConfig, val node : Node) {

  /** SqlSource built from an XML Node */
  def build : SqlSource =
    new DynamicSqlSource(configuration, parse(node))

  private def parse(n : Node) : SqlNode = {
    n match {
      case Text(text) =>
        new TextSqlNode(text)
      case PCData(text) =>
        new TextSqlNode(text)
      case <xsql>{children @ _*}</xsql> =>
        parseChildren(children)
      case trim @ <trim>{children @ _*}</trim> =>
        val content = parseChildren(children)
        new TrimSqlNode(
          configuration,
          content,
          attr(trim, "@prefix"),
          attr(trim, "@prefixOverrides"),
          attr(trim, "@suffix"),
          attr(trim, "@suffixOverrides")
        )
      case <where>{children @ _*}</where> =>
        val content = parseChildren(children)
        new WhereSqlNode(configuration, content)
      case <set>{children @ _*}</set> =>
        val content = parseChildren(children)
        new SetSqlNode(configuration, content)
      case foreach @ <foreach>{children @ _*}</foreach> =>
        val content = parseChildren(children)
        new ForEachSqlNode(
          configuration,
          content,
          attr(foreach, "@collection"),
          attr(foreach, "@item"),
          attr(foreach, "@index"),
          attr(foreach, "@open"),
          attr(foreach, "@close"),
          attr(foreach, "@separator"))
      case ifNode @ <if>{children @ _*}</if> =>
        val content = parseChildren(children)
        new IfSqlNode(content, attr(ifNode, "@test"))
      case <choose>{children @ _*}</choose> =>
        val ifNodes = new ArrayList[IfSqlNode]
        var defaultNode : MixedSqlNode = null
        for (child <- children) {
          child match {
            case when @ <when>{ch @ _*}</when> => {
              val content = parseChildren(ch)
              ifNodes add new IfSqlNode(content, attr(when, "@test"))
            }
            case other @ <otherwise>{ch @ _*}</otherwise> =>
              if (defaultNode == null)
                defaultNode = parseChildren(ch)
              else
                throw new ConfigurationException("Too many default (otherwise) elements in choose statement.")
                //error("Too many default (otherwise) elements in choose statement.")
          }
        }
        new ChooseSqlNode(ifNodes, defaultNode)
      case ifNode @ <when>{children @ _*}</when> =>
        val content = parseChildren(children)
        new IfSqlNode(content, attr(ifNode, "@test"))
      case other @ <otherwise>{children @ _*}</otherwise> =>
        parseChildren(other)
      case a : Atom[String] =>
        new TextSqlNode(a.data)
      case unsupported =>
        throw new ConfigurationException("Unknown element " + unsupported.getClass.getName + " in SQL statement.")
        //error("Unknown element " + unsupported.toString + " in SQL statement.")
    }
  }

  private def parseChildren(children : Seq[Node]) : MixedSqlNode = {
    val nodes = new ArrayList[SqlNode]
    for (child <- children) {
      nodes add parse(child)
    }
    new MixedSqlNode(nodes)
  }

  private def attr(n : Node, name : String) : String = {
    (n \ name).text match {
      case "" => null
      case text => text
    }
  }

}
