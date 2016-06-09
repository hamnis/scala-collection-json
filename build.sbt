organization := "net.hamnaberg.rest"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.1")

scalacOptions := Seq("-deprecation")

description := "Collection+JSON"

name := "scala-json-collection"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.13" % "test"
