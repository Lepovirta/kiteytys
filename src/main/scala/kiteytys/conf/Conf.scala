package kiteytys.conf

import com.typesafe.config._
import java.io.File

object Conf {
  private val default = ConfigFactory.load()

  final case class Http(conf: Config) {
    val port = conf.getInt("port")
  }

  final case class Email(conf: Config) {
    val responseSubject = conf.getString("responseSubject")
    val responseMessage = conf.getString("responseMessage")
    val adminSubject = conf.getString("adminSubject")
    val attachmentName = conf.getString("attachmentName")
    val attachmentDescription = conf.getString("attachmentDescription")
    val adminEmail = conf.getString("adminEmail")
    val fromName = conf.getString("fromName")
    val fromEmail = conf.getString("fromEmail")

    val smtp = Smtp(conf.getConfig("smtp"))
  }

  final case class Smtp(conf: Config) {
    val host       = conf.getString("host")
    val port       = conf.getInt("port")
    val user       = conf.getString("username")
    val password   = conf.getString("password")
    val ssl        = conf.getBoolean("ssl")
  }

  final case class Database(conf: Config) {
    val driver   = conf.getString("driver")
    val url      = conf.getString("url")
    val username = conf.getString("username")
    val password = conf.getString("password")
  }

  final case class Pdf(conf: Config) {
    val path = new File(conf.getString("path"))
  }

  def load(path: String): Conf = {
    val file   = new File(path)
    val custom = ConfigFactory.parseFile(file)
    Conf(custom.withFallback(default))
  }
}

final case class Conf(conf: Config) {
  import Conf._

  val http         = Http(conf.getConfig("http"))
  val pdf          = Pdf(conf.getConfig("pdf"))
  val email        = Email(conf.getConfig("email"))
  val database     = Database(conf.getConfig("database"))
}
