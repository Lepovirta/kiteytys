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
  val port         = conf.getInt("port")
  val pdfPath      = conf.getString("pdf_path")
  val smtp         = Smtp(conf.getConfig("smtp"))
  val database     = Database(conf.getConfig("database"))
}

final case class Smtp(conf: Config) {
  val host     = conf.getString("host")
  val port     = conf.getInt("port")
  val from     = conf.getString("from")
  val user     = conf.getString("username")
  val password = conf.getString("password")
}

final case class Database(conf: Config) {
  val driver = conf.getString("driver")
  val url = conf.getString("url")
  val username = conf.getString("username")
  val password = conf.getString("password")
}