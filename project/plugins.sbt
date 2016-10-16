logLevel := Level.Info

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.5.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.2")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "org.apache.velocity" % "velocity" % "1.7"
)
