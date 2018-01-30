publishTo := {
  val v = (version in ThisBuild).value

  if (v.trim().endsWith("SNAPSHOT")) Some(Opts.resolver.sonatypeSnapshots) else Some(Opts.resolver.sonatypeStaging)
}

pomIncludeRepository := { x => false }

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

homepage := Some(new URL("http://github.com/hamnis/scala-collection-json"))

startYear := Some(2014)

licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))

scmInfo := Some(ScmInfo(
  new URL("http://github.com/hamnis/scala-collection-json"),
  "scm:git:git://github.com/hamnis/scala-collection-json.git",
  Some("scm:git:git@github.com:hamnis/scala-collection-json.git")
))

developers += Developer(
  "hamnis",
  "Erlend Hamnaberg",
  "erlend@hamnaberg.net",
  new URL("http://twitter.com/hamnis")
)


enablePlugins(SignedAetherPlugin)

disablePlugins(AetherPlugin)

overridePublishSignedBothSettings
