name := "json-collection"

organization := "net.hamnaberg.rest"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.3",
  "org.codehaus.jackson" % "jackson-core-asl" % "1.8.3",
  "com.google.guava" % "guava" % "r09",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "junit" % "junit" % "4.8.2" % "test"
)
