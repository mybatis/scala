/*
 *    Copyright 2011-2015 the original author or authors.
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

import org.apache.ibatis.session.{Configuration => MBConfig}
import java.util.{List, ArrayList}
import org.apache.ibatis.scripting.xmltags._
import org.apache.ibatis.mapping.SqlSource
import scala.xml._

/** Builder of Dynamic SQL Trees. */
private[scala] class DynamicSQLBuilder(val configuration : MBConfig, val node : Node) {

  /** SqlSource built from an XML Node */
  def build : SqlSource =
    new DynamicSqlSource(configuration, parse(node))

  private def isXmlWhitespace(text : String) : Boolean =
    text.forall(c => c == ' ' || c == '\t' || c == '\r' || c == '\n')

  private def parse(n : Node) : SqlNode = {
    n match {
      case Text(text) =>
        new TextSqlNode(text)
      case PCData(text) =>
        new TextSqlNode(text)
      case elem : Elem =>
        elem.label match {
          case "xsql" =>
            parseChildren(elem.child)
          case "trim" =>
            val content = parseChildren(elem.child)
            new TrimSqlNode(
              configuration,
              content,
              attr(elem, "@prefix"),
              attr(elem, "@prefixOverrides"),
              attr(elem, "@suffix"),
              attr(elem, "@suffixOverrides")
            )
          case "where" =>
            new WhereSqlNode(configuration, parseChildren(elem.child))
          case "set" =>
            new SetSqlNode(configuration, parseChildren(elem.child))
          case "foreach" =>
            val content = parseChildren(elem.child)
            new ForEachSqlNode(
              configuration,
              content,
              attr(elem, "@collection"),
              attr(elem, "@index"),
              attr(elem, "@item"),
              attr(elem, "@open"),
              attr(elem, "@close"),
              attr(elem, "@separator"))
          case "if" =>
            new IfSqlNode(parseChildren(elem.child), attr(elem, "@test"))
          case "choose" =>
            val ifNodes = new ArrayList[SqlNode]
            var defaultNode : MixedSqlNode = null
            for (child <- elem.child) {
              child match {
                case when : Elem if when.label == "when" =>
                  ifNodes add new IfSqlNode(parseChildren(when.child), attr(when, "@test"))
                case otherwise : Elem if otherwise.label == "otherwise" =>
                  if (defaultNode == null)
                    defaultNode = parseChildren(otherwise.child)
                  else
                    throw new ConfigurationException("Too many default (otherwise) elements in choose statement.")
                case Text(text) if isXmlWhitespace(text) =>
                case PCData(text) if isXmlWhitespace(text) =>
                case other =>
                  throw new ConfigurationException("Invalid content in choose statement: " + other.getClass.getName + ".")
              }
            }
            new ChooseSqlNode(ifNodes, defaultNode)
          case "when" =>
            new IfSqlNode(parseChildren(elem.child), attr(elem, "@test"))
          case "otherwise" =>
            parseChildren(elem.child)
          case "bind" =>
            new VarDeclSqlNode(attr(elem, "@name"), attr(elem, "@value"))
          case _ =>
            throw new ConfigurationException("Unknown element " + elem.label + " in SQL statement.")
        }
      case a : Atom[_] =>
        new TextSqlNode(a.data.asInstanceOf[String])
      case unsupported =>
        throw new ConfigurationException("Unknown element " + unsupported.getClass.getName + " in SQL statement.")
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
