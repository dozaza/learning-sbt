import java.io.{File, StringWriter}
import java.util

import scala.xml._
import scala.collection.convert.wrapAsJava._

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity


case class ScalaStyleError(
                          name: String,
                          line: String,
                          level: String,
                          message: String
                          )


object ScalaStyleReport {

  def report(outputDir: File, outputFile: String, templateFile: File, reportXml: File): File = {
    def attr(node: Node, name: String) = (node \\ ("@" + name)).text

    val xml = XML.loadFile(reportXml)

    val errors = asJavaCollection((xml \\ "checkstyle" \\ "file").flatMap(f => {
      val name = attr(f, "name")
      (f \\ "error").map { e =>
        val line = attr(e, "line")
        val severity = attr(e, "severity")
        val message = attr(e, "message")
        ScalaStyleError(name, line, severity, message)
      }
    }))

    sbt.IO.createDirectory(outputDir)

    val context = new util.HashMap[String, Any]()
    context.put("results", errors)

    val sw = new StringWriter()
    val template = sbt.IO.read(templateFile)
    Velocity.evaluate(new VelocityContext(context), sw, "velocity", template)

    val reportFile = new File(outputDir, outputFile)
    sbt.IO.write(reportFile, sw.toString)
    reportFile
  }

}
