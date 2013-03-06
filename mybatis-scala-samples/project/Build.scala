import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "org.mybatis.scala",
    version      := "1.0.1",
    scalaVersion := "2.10.0"
  )
}

object Resolvers {
  val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatype = "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val mybatis = "org.mybatis.scala" %% "mybatis-scala-core" % "1.0.1"
  val hsqldb = "org.hsqldb" % "hsqldb" % "2.2.8"
  val log4j = "log4j" % "log4j" % "1.2.17"
}

object MainBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val deps = Seq(mybatis, hsqldb, log4j)
  val coreResolvers = Seq(sonatypeSnapshots, sonatype)

  lazy val samples = Project(
    "mybatis-scala-samples",
    file("."),
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies ++= deps)
  )

}
