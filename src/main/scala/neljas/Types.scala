package neljas

import org.http4s.UrlForm
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

object Game {
  import FormParsing._

  def fromForm(form: UrlForm): Either[String, Game] = {
    val result = (
      stringField(form, "identifier")    |@|
      stringField(form, "email")         |@|
      stringField(form, "strongCard")    |@|
      intField(form, "strongNum")        |@|
      stringField(form, "weakCard")      |@|
      intField(form, "weakNum")          |@|
      stringField(form, "importantCard") |@|
      intField(form, "importantNum")     |@|
      stringField(form, "hardCard")      |@|
      intField(form, "hardNum")          |@|
      stringField(form, "tediousCard")   |@|
      intField(form, "tediousNum")       |@|
      stringField(form, "inspiringCard") |@|
      intField(form, "inspiringNum")     |@|
      stringField(form, "topaasia")      |@|
      stringField(form, "openQuestion")  |@|
      intField(form, "rating")
    )(Game.apply)
    resultToEither(result)
  }
}

final case class Game(
  identifier: String, email: String, strongCard: String, strongNum: Int,
  weakCard: String, weakNum: Int, importantCard: String, importantNum: Int,
  hardCard: String, hardNum: Int, tediousCard: String, tediousNum: Int,
  inspiringCard: String, inspiringNum: Int, topaasia: String,
  openQuestion: String, rating: Int
)
