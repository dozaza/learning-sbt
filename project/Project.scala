import sbt._

object BuildUtils {

 def loggingDependencies(): Seq[sbt.ModuleID] = {
   Seq("com.typesafe.scala-logging" %% "scala-logging" % "3.0.0")
 }

}
