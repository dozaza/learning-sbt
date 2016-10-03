import sbt.complete.Parser
import AssemblyKeys._

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

testOptions in Test += Tests.Argument("html")

// used for creating html test report
libraryDependencies += "org.pegdown" % "pegdown" % "1.0.2" % "test"

javaOptions in Test += "-Dspecs2.outDir=" + (target.value / "generated/test-reports").getAbsolutePath

fork in Test := true

val dependentJarDirectory = settingKey[File]("location of the unpacked dependent jars")

dependentJarDirectory := target.value / "dependent-jars"

val createDependentJarDirectory = taskKey[File]("create the dependent-jar directory")

createDependentJarDirectory := {
  sbt.IO.createDirectory(dependentJarDirectory.value)
  dependentJarDirectory.value
}

val excludes = List("meta-inf", "license", "play.plugins", "reference.conf")

def unpackFilter(target: File) = new NameFilter {
  override def accept(name: String): Boolean = {
    !excludes.exists(f => name.toLowerCase.startsWith(f)) && !file(target.getAbsolutePath + "/" + name).exists
  }
}

def unpack(target: File, file: File, log: Logger) = {
  log.debug("unpacking " + file.getName)
  if (file.isDirectory) {
    sbt.IO.copyDirectory(file, target)
  } else {
    sbt.IO.unzip(file, target, filter = unpackFilter(target))
  }
}

def create(dir: File, buildJar: File) = {
  val files = (dir ** "*").get.filter(_ != dir)
  val filesWithPath = files.map { f =>
    (f, f.relativeTo(dir).get.getPath)
  }
  sbt.IO.zip(filesWithPath, buildJar)
}

val createUberJar = taskKey[File]("create jar which we will run")

createUberJar := {
  val output = target.value / "build.jar"
  create(dependentJarDirectory.value, output)
  output
}

val printCp = taskKey[Unit]("print class path")

printCp := {
  val logger = streams.value.log
  TestProjectScala.println((fullClasspath in Compile).value, logger)
}

val dbQuery = inputKey[Unit]("Run a db query")

val queryParser: Parser[String] = {
  import complete.DefaultParsers._
  token(any.*.map(_.mkString))
}

dbQuery := {
  val logger = streams.value.log
  val query = queryParser.parsed
  logger.info(query)
  throw new RuntimeException("Test error!!!!")
  query
}

org.scalastyle.sbt.ScalastylePlugin.Settings

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case "application.conf" => MergeStrategy.concat
    case x => old(x)
  }
}

excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter { f =>
    (f.data.getName contains "commons-logging") ||
      (f.data.getName contains "sbt-linK")
  }
}

mainClass in assembly := Some("Global")

val scalaStyleReport = taskKey[File]("run scala style and create a report")

scalaStyleReport := {
  val result = org.scalastyle.sbt.PluginKeys.scalastyle.toTask("").value
  ???
}
