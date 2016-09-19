import sbt._
import sbt.Keys._
import sbt.Logger

object TestProjectScala {

  def println(cp: Classpath, logger: Logger): Unit = {
    logger.info(cp.toString)
  }

}
