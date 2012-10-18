// IDEA plugin
resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.7")
