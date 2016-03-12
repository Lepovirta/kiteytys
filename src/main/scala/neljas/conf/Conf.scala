package neljas.conf

import com.typesafe.config._
import java.io.File

object Conf {
  private val default = ConfigFactory.load()

  def load(path: String): Settings = {
    val file   = new File(path)
    val custom = ConfigFactory.parseFile(file)
    Settings(custom.withFallback(default))
  }
}

case class Settings(conf: Config) {
  val port    = conf.getInt("port")
  val pdfPath = conf.getString("pdf_path")

  val smtpHost     = conf.getString("smtp.host")
  val smtpPort     = conf.getInt("smtp.port")
  val smtpFrom     = conf.getString("smtp.from")
  val smtpUser     = conf.getString("smtp.user")
  val smtpPassword = conf.getString("smtp.passwd")
}
