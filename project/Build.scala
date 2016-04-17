/**
 *    Copyright 2011-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "org.mybatis.scala",
    version      := "1.0.3",
    scalaVersion := "2.11.6"
  )
  val mybatisVersion = "3.2.8"
}

object Resolvers {
  val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatype = "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val mybatis = "org.mybatis" % "mybatis" % BuildSettings.mybatisVersion
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.3" % "test"
  val hsqldb = "org.hsqldb" % "hsqldb" % "2.3.2"
  val scalaxml = "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3"
}

object MainBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val coreDeps = Seq(mybatis, scalaxml, scalatest, hsqldb % "test" )
  val coreResolvers = Seq(sonatypeSnapshots, sonatype)

  lazy val root = Project(
    "mybatis-scala-parent",
    file("."),
    settings = buildSettings
  ) aggregate(core, samples)

  lazy val core = Project(
    "mybatis-scala-core",
    file("mybatis-scala-core"),
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies ++= coreDeps)
  )

  lazy val samples = Project(
    "mybatis-scala-samples",
    file("mybatis-scala-samples"),
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies ++= Seq(hsqldb))
  ) dependsOn(core)

}
