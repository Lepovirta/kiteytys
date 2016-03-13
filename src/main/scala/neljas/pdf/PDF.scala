package neljas.pdf

import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.scalalogging.LazyLogging
import io.github.cloudify.scala.spdf._
import java.io._
import java.util.concurrent.Executors
import neljas.conf.Conf

import scalaz.concurrent.Task

final class PDF(conf: Conf.Pdf) extends LazyLogging {

  implicit val pdfThreadPool = Executors.newFixedThreadPool(2)

  val pdf = Pdf(new PdfConfig {
    userStyleSheet := "static/pdf.css"
    marginTop := "0mm"
    marginBottom := "0mm"
    marginLeft := "0mm"
    marginRight := "0mm"
  })

  def generate(page: String): Task[Array[Byte]] = Task.fork {
    val output = new ByteArrayOutputStream
    val result = pdf.run(page, output)

    if (result == 0) Task.now(output.toByteArray)
    else Task.fail(new RuntimeException(s"PDF generation failed. Result code: $result"))
  }

  def save(pdf: Array[Byte]): Task[String] = Task.fork {
    val timestamp = new SimpleDateFormat("yyyyMMdd-hh:mm:ss").format(new Date)
    val filename = timestamp + ".pdf"
    val fullPath = new File(conf.path, filename)
    val bos = new BufferedOutputStream(new FileOutputStream(fullPath))

    try {
      bos.write(pdf)
      bos.flush()
      logger.info(s"Wrote PDF to ${fullPath.getPath}")
      Task.now(fullPath.getPath)
    } catch {
      case ex: IOException => Task.fail(ex)
    } finally {
      bos.close()
    }
  }
}
