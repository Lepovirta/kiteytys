package neljas.pdf

import io.github.cloudify.scala.spdf._
import java.io._

object PDF {
  val pdf = Pdf(new PdfConfig {
    userStyleSheet := "static/pdf.css"
    marginTop := "0mm"
    marginBottom := "0mm"
    marginLeft := "0mm"
    marginRight := "0mm"
  })

  // TODO: error handlind?
  def generate(page: String) = {
    val output = new ByteArrayOutputStream
    val _ = pdf.run(page, output)
    output.toByteArray
  }
}
