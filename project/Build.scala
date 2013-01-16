import sbt._
import sbt.Keys._
import xml.Group
import aether._

object Build extends sbt.Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Aether.aetherPublishSettings ++ Seq(
    organization := "net.hamnaberg.rest",
    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0"),
    scalacOptions := Seq("-deprecation"),
    publishTo <<= (version) apply {
      (v: String) => if (v.trim().endsWith("SNAPSHOT")) Some(Resolvers.sonatypeNexusSnapshots) else Some(Resolvers.sonatypeNexusStaging)
    },
    pomIncludeRepository := { x => false },
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
  )

  lazy val root = Project(
    id = "json-collection",
    base = file("."),
    settings = buildSettings ++ Seq(
      description := "Collection+JSON",
      name := "scala-json-collection", 
      libraryDependencies += "org.json4s" %% "json4s-native" % "3.1.0",
      libraryDependencies <+= scalaVersion { sv =>
          val ver = if (sv.startsWith("2.10")) "1.13" else "1.12.3"
          "org.specs2" %% "specs2" % ver % "test"
      },
	    manifestSetting
	    ) ++ mavenCentralFrouFrou
	  )

	  object Resolvers {
	    val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	    val sonatypeNexusStaging = "Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
	  }

	  lazy val manifestSetting = packageOptions <+= (name, version, organization) map {
	    (title, version, vendor) =>
	      Package.ManifestAttributes(
	        "Created-By" -> "Simple Build Tool",
	        "Built-By" -> System.getProperty("user.name"),
	        "Build-Jdk" -> System.getProperty("java.version"),
	        "Specification-Title" -> title,
	        "Specification-Version" -> version,
	        "Specification-Vendor" -> vendor,
	        "Implementation-Title" -> title,
	        "Implementation-Version" -> version,
	        "Implementation-Vendor-Id" -> vendor,
	        "Implementation-Vendor" -> vendor
	      )
	  }

	  // Things we care about primarily because Maven Central demands them
	  lazy val mavenCentralFrouFrou = Seq(
	    homepage := Some(new URL("http://github.com/hamnis/scala-collection-json/")),
	    startYear := Some(2011),
	    licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))),
	    pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
	      <scm>
	        <url>http://github.com/hamnis/scala-collection-json</url>
	        <connection>scm:git:git://github.com/hamnis/scala-collection-json.git</connection>
	        <developerConnection>scm:git:git@github.com:hamnis/scala-collection-json.git</developerConnection>
	      </scm>
	      <developers>
	        <developer>
	          <id>hamnis</id>
	          <name>Erlend Hamnaberg</name>
	          <url>http://twitter.com/hamnis</url>
	        </developer>
	      </developers>
	    )}
	  )
	}
