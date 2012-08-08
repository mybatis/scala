import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "org.mybatis.scala",
    version      := "1.0-beta3",
    scalaVersion := "2.9.2"
  )
}

object Resolvers {
  val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatype = "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val mybatis = "org.mybatis.scala" %% "mybatis-scala-core" % "1.0-beta3"
  val hsqldb = "org.hsqldb" % "hsqldb" % "2.2.8"
}

object MainBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val deps = Seq(mybatis, hsqldb)
  val coreResolvers = Seq(sonatypeSnapshots, sonatype)

  lazy val samples = Project(
    "mybatis-scala-samples",
    file("."),
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies ++= deps)
  )

}
