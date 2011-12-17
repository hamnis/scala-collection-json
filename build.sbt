name := "json-collection"

organization := "net.hamnaberg.rest"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "net.liftweb" %% "lift-json" % "2.4-M4"
)
