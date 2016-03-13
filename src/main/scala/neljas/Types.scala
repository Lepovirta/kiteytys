package neljas

import org.http4s.UrlForm
import argonaut._
import Argonaut._
import scala.util.Try
import scalaz.Scalaz._
import scalaz.ValidationNel


object FormParsing {
  def resultToEither[A](result: ValidationNel[String, A]): Either[String, A] =
    result.toEither.leftMap(_.list.mkString(". "))

  def stringField(form: UrlForm, field: String): ValidationNel[String, String] =
    form.getFirst(field)
      .toSuccess(s"Missing field '$field'")
      .toValidationNel

  def intField(form: UrlForm, field: String): ValidationNel[String, Int] =
    form.getFirst(field)
      .flatMap(v => Try(v.toInt).toOption)
      .toSuccess(s"Invalid format for field '$field'")
      .toValidationNel
}

object User {
  import FormParsing._

  def fromForm(form: UrlForm): Either[String, User] = {
    val result = (stringField(form, "name") |@| intField(form, "age") |@| stringField(form, "email"))(User.apply)
    resultToEither(result)
  }
}

final case class User(name: String, age: Int, email: String)

object Message {
  import FormParsing._

  def fromForm(form: UrlForm): Either[String, Message] = {
    val result = (stringField(form, "title") |@| stringField(form, "content"))(Message.apply)
    resultToEither(result)
  }

  implicit val messageJsonCodec: CodecJson[Message] =
    casecodec2(Message.apply, Message.unapply)("title", "content")
}

final case class Message(title: String, content: String)