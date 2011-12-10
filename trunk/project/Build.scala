import sbt._
import Keys._

object MainBuild extends Build {

        lazy val root = Project(id = "mybatis-scala-parent", base = file(".")) aggregate(core, samples)

        lazy val core = Project(id = "mybatis-scala-core", base = file("mybatis-scala-core"))

        lazy val samples = Project(
          id = "mybatis-scala-samples",
          base = file("mybatis-scala-samples")) dependsOn(core)

}
