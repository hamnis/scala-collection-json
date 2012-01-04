import sbt._
import sbt.Keys._
import sbtrelease.Release._
import sbtrelease.ReleasePart
import sbtrelease.ReleaseKeys._

object AtomClient extends Build {

  val liftJSONversion = "2.4-M4"

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "net.hamnaberg.rest",
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    crossScalaVersions := Seq("2.9.1")
  )

  lazy val root = Project(
    id = "json-collection",
    base = file("."),
    settings = buildSettings ++ releaseSettings ++ Seq(
      description := "Collection+JSON",
      name := "json-collection", 
      libraryDependencies := Seq(
        "net.liftweb" %% "lift-json" % liftJSONversion,
        "org.specs2" %% "specs2" % "1.6.1" % "test"
      ),

      releaseProcess <<= thisProjectRef apply { ref =>
        import sbtrelease.ReleaseStateTransformations._
        Seq[ReleasePart](
          initialGitChecks,
          checkSnapshotDependencies,
          inquireVersions,
          runTest,
          setReleaseVersion,
          commitReleaseVersion,
          tagRelease,
        // Enable when we're deploying to Sonatype
  //        releaseTask(publish in Global in ref),
          setNextVersion,
          commitNextVersion
        )
      }
    )
  )
}
