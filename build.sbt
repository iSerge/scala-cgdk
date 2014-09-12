import org.scalastyle.sbt.{ScalastylePlugin, PluginKeys}

name := "scala-cgdk"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-deprecation", "-feature")

mainClass in (Compile, packageBin) := Some("Runner")

mainClass in (Compile, run) := Some("Runner")

ScalastylePlugin.Settings

PluginKeys.config := file("project/scalastyle-config.xml")
