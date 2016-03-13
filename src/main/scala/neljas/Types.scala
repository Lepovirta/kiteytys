package neljas

import org.http4s.UrlForm
import scala.util.Try

object FormParsing {
  def stringField(form: UrlForm, field: String): Either[String, String] =
    form.getFirst(field).toRight(s"Missing field '$field'")

  def intField(form: UrlForm, field: String,
               min: Int = Int.MinValue, max: Int = Int.MaxValue): Either[String, Int] =
    form.getFirst(field)
      .flatMap(v => Try(v.toInt).toOption)
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

  def fromForm(form: UrlForm): Either[String, Game] =
    for {
      identifier <-stringField(form, "identifier").right
      email <-stringField(form, "email").right
      strong <-cardFromForm(form, "strong").right
      weak <-cardFromForm(form, "weak").right
      important <-cardFromForm(form, "important").right
      hard <-cardFromForm(form, "hard").right
      tedious <-cardFromForm(form, "tedious").right
      inspiring <-cardFromForm(form, "inspiring").right
      topaasia <-stringField(form, "topaasia").right
      openQuestion <-stringField(form, "openQuestion").right
      rating <-intField(form, "rating").right
    } yield Game(identifier, email, strong, weak, important, hard, inspiring, tedious, topaasia, openQuestion, rating)

  private def cardFromForm(form: UrlForm, level: String) = for {
    card <- stringField(form, s"${level}Card").right
    grade <- intField(form, s"${level}Num", min = 0, max = 4).right
  } yield Card(card, grade)
}

final case class Game(
  identifier: String,
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
