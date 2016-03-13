package neljas

import org.http4s.UrlForm

import scala.util.Try
import scalaz.Scalaz._

import neljas.conf.Settings

object User {
  def fromForm(form: UrlForm): Either[String, User] = {
    val result = validateForm(form)(User.apply)
    result.toEither.leftMap(_.list.mkString(". "))
  }

  private def validateForm(form: UrlForm) =
    stringField(form, "name") |@| intField(form, "age") |@| stringField(form, "email")

  private def stringField(form: UrlForm, field: String) =
    form.getFirst(field)
      .toSuccess(s"Missing field '$field'")
      .toValidationNel

  private def intField(form: UrlForm, field: String) =
    form.getFirst(field)
      .flatMap(v => Try(v.toInt).toOption)
      .toSuccess(s"Invalid format for field '$field'")
      .toValidationNel
}

final case class User(name: String, age: Int, email: String)

final case class EmailData(conf: Settings, toAddr: String, path: String) {
  val host = conf.smtpHost
  val port = conf.smtpPort
  val user = conf.smtpUser
  val password = conf.smtpPassword
  val to = toAddr
  val toName = to
  val from = conf.smtpFrom
  val fromName = conf.smtpFromName
  val filePath = path
}
