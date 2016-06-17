publishTo <<= (version) apply {
  (v: String) => if (v.trim().endsWith("SNAPSHOT")) Some(Opts.resolver.sonatypeSnapshots) else Some(Opts.resolver.sonatypeStaging)
}

pomIncludeRepository := { x => false }

packageOptions <+= (name, version, organization) map {
  (title, version, vendor) =>
    Package.ManifestAttributes(
      "Created-By" -> "Scala Build Tool",
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

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

homepage := Some(new URL("http://github.com/hamnis/scala-collection-json"))

startYear := Some(2014)

licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))

pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ xml.Group(
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

enablePlugins(SignedAetherPlugin)

disablePlugins(AetherPlugin)

overridePublishSignedBothSettings
