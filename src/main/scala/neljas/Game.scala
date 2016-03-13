package neljas

import org.http4s.UrlForm
import scala.util.Try

object FormParsing {
  def stringToInt(s: String): Option[Int] = Try(s.toInt).toOption

  def stringField(form: UrlForm, field: String): Either[String, String] =
    form.getFirst(field).toRight(s"Missing field '$field'")

  def intField(form: UrlForm, field: String,
               min: Int = Int.MinValue, max: Int = Int.MaxValue): Either[String, Int] =
    form.getFirst(field)
      .flatMap(stringToInt)
      .toRight(s"Invalid format for field '$field'")
      .right.flatMap { i =>
        if (i < min) Left(s"Number $i should be greater or equal than $min")
        else Right(i)
      }
      .right.flatMap { i =>
        if (i > max) Left(s"Number $i should be less or equal than $max")
        else Right(i)
      }
}

object Game {
  import FormParsing._

  private val trailingNumber = "\\d+$".r
  private val minCardNumber = 1
  private val maxCardrNumber = 52

  def fromForm(form: UrlForm): Either[String, Game] =
    for {
      owner <- stringField(form, "identifier").right
      email <- stringField(form, "email").right
      strong <- cardFromForm(form, "strong").right
      weak <- cardFromForm(form, "weak").right
      important <- cardFromForm(form, "important").right
      hard <- cardFromForm(form, "hard").right
      tedious <- cardFromForm(form, "tedious").right
      inspiring <- cardFromForm(form, "inspiring").right
      topaasia <- stringField(form, "topaasia").right
      openQuestion <- stringField(form, "openQuestion").right
      rating <- intField(form, "rating", min = 1, max = 4).right
    } yield Game(owner, email, strong, weak, important, hard, inspiring, tedious, topaasia, openQuestion, rating)

  private def cardFromForm(form: UrlForm, level: String) = for {
    card <- cardName(form, s"${level}Card").right
    grade <- intField(form, s"${level}Num", min = 1, max = 4).right
  } yield Card(card, grade)

  private def cardName(form: UrlForm, field: String) =
    stringField(form, field)
      .right.map(_.toUpperCase)
      .right.flatMap { s =>
        trailingNumber
          .findFirstIn(s)
          .flatMap(stringToInt)
          .filter(n => n >= minCardNumber && n <= maxCardrNumber)
          .map(_ => s)
          .toRight(s"Card name '$s' should end with a number between $minCardNumber-$maxCardrNumber.")
      }
}

final case class Game(
  owner: String,
  email: String,
  strong: Card,
  weak: Card,
  important: Card,
  hard: Card,
  tedious: Card,
  inspiring: Card,
  topaasia: String,
  openQuestion: String,
  rating: Int
)

final case class Card(name: String, grade: Int)
