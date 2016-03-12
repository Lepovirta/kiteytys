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

final case class Settings(conf: Config) {
  val smtpConf = conf.getConfig("smtp")

  val port         = conf.getInt("port")
  val pdfPath      = conf.getString("pdf_path")
  val smtpHost     = smtpConf.getString("host")
  val smtpPort     = smtpConf.getInt("port")
  val smtpFrom     = smtpConf.getString("from")
  val smtpUser     = smtpConf.getString("user")
  val smtpPassword = smtpConf.getString("passwd")
}
