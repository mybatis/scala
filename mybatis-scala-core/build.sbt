name := "mybatis-scala-core"

libraryDependencies ++= Seq(
        "org.mybatis" % "mybatis" % "3.1.0-SNAPSHOT",
        "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-releases"
)
