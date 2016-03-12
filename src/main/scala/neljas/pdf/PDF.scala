package neljas.pdf

import io.github.cloudify.scala.spdf._
import java.io._

object PDF {
  // Create a new Pdf converter with a custom configuration
  // run `wkhtmltopdf --extended-help` for a full list of options
  val pdf = Pdf(new PdfConfig {
    orientation := Landscape
    pageSize := "Letter"
    marginTop := "1in"
    marginBottom := "1in"
    marginLeft := "1in"
    marginRight := "1in"
  })

  // TODO: error handlind?
  def generate(page: String) = {
    val output = new ByteArrayOutputStream
    val _ = pdf.run(page, output)
    output.toByteArray
  }
}
