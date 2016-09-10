name := "learning-sbt"

organization := "com.github.dozaza"

version := "1.0"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "test",
  "org.specs2" % "specs2_2.10" % "2.1.1" % "test"
//  organization.value % "core-library" % version.value
)

val gitHeadCommitSha = taskKey[String](
  "Determines the current git commit SHA"
)

gitHeadCommitSha := Process("git rev-parse HEAD").lines.head

val makeVersionProperties = taskKey[Seq[File]] (
  "Make a version.properties file."
)

makeVersionProperties := {
  val propFile = new File((resourceManaged in Compile).value, "version.properties")
  val content = "version=%s" format gitHeadCommitSha.value
  IO.write(propFile, content)
  Seq(propFile)
}

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
