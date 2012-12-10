import sbt._
import Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "org.mybatis.scala",
    version      := "1.0.0",
    scalaVersion := "2.9.2"
  )
}

object Resolvers {
  val sonatypeSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatype = "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val mybatis = "org.mybatis" % "mybatis" % "3.1.0"
  val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
}

object MainBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  val coreDeps = Seq(mybatis, scalatest)
  val coreResolvers = Seq(sonatypeSnapshots, sonatype)

  lazy val core = Project(
    "mybatis-scala-core",
    file("."),
    settings = buildSettings ++ Seq(resolvers := coreResolvers, libraryDependencies ++= coreDeps)
  )

}
