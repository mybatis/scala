import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "org.mybatis.scala",
    version      := "1.0-beta1",
    scalaVersion := "2.9.1"
  )
}

object Resolvers {
  val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatype = "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val mybatis = "org.mybatis" % "mybatis" % "3.1.0"
  val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  val postgresql = "postgresql" % "postgresql" % "9.0-801.jdbc4"
  val hsqldb = "org.hsqldb" % "hsqldb" % "2.2.8"
}

object MainBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val coreDeps = Seq(mybatis, scalatest)
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
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies += hsqldb)
  ) dependsOn(core)

}
