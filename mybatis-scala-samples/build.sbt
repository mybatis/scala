name := "mybatis-scala-samples"

libraryDependencies += "postgresql" % "postgresql" % "9.0-801.jdbc4"

mainClass := Some("org.mybatis.scala.samples.SelectWithResultMapSample")

resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-releases"
)
