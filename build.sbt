name := "scala-cgdk"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

mainClass in (Compile, packageBin) := Some("Runner")

mainClass in (Compile, run) := Some("Runner")

org.scalastyle.sbt.ScalastylePlugin.Settings

org.scalastyle.sbt.PluginKeys.config := file("project/scalastyle-config.xml")
