// same as aritfactId in Maven
name := "learning-sbt"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "test",
  "org.specs2" % "specs2_2.10" % "2.1.1" % "test",
  // This will produce conflict
//  "org.specs2" % "specs2_2.9.1" % "1.10" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "ch.qos.logback" % "logback-classic" % "1.0.7"
//  organization.value % "core-library" % version.value
)

val gitHeadCommitSha = taskKey[String](
  "Determines the current git commit SHA"
)

gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head

val makeVersionProperties = taskKey[Seq[File]] (
  "Make a version.properties file."
)

val taskA = taskKey[Unit]("taskA")
val taskB = taskKey[Unit]("taskB")
val taskC = taskKey[Unit]("taskC")
// run task in parallel
taskA := {
  val b = taskB.value
  val c = taskC.value
  println("taskA")
}
taskB := {
  Thread.sleep(5000)
  println("taskB")
}
taskC := {
  Thread.sleep(5000)
  println("taskC")
}

lazy val core_library = learningSbtProject("core-library").settings()

lazy val common = learningSbtProject("common").settings(
  makeVersionProperties := {
    val propFile = (resourceManaged in Compile).value / "version.properties"
    val content = "version=%s" format gitHeadCommitSha.value
    IO.write(propFile, content)
    Seq(propFile)
  }
)

lazy val analytics = learningSbtProject("analytics").dependsOn(common).settings()

lazy val website = learningSbtProject("website").dependsOn(common).settings()

def learningSbtProject(name: String): Project = {
  Project(name, file(name)).settings(
    version := "1.0.0g",
    // Same as groupId in Maven
    organization := "com.github.dozaza",
    libraryDependencies +=  "org.specs2" % "specs2_2.10" % "2.1.1" % "test"
  )
}
