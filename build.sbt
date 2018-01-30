organization := "net.hamnaberg.rest"

crossScalaVersions := Seq("2.12.4", "2.11.12", "2.10.7")

scalaVersion := crossScalaVersions.value.head

scalacOptions := Seq("-deprecation")

description := "Collection+JSON"

name := "scala-json-collection"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.9" % "test"
