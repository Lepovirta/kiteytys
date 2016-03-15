package kiteytys

import java.time.LocalDateTime

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
  private val maxCardNumber = 52

  def fromForm(form: UrlForm): Either[String, GameInput] =
    for {
      owner <- stringField(form, "owner").right
      email <- stringField(form, "email").right
      strong <- cardFromForm(form, "strong").right
      weak <- cardFromForm(form, "weak").right
      important <- cardFromForm(form, "important").right
      hard <- cardFromForm(form, "hard").right
      tedious <- cardFromForm(form, "tedious").right
      inspiring <- cardFromForm(form, "inspiring").right
      topaasia <- stringField(form, "topaasia").right
      openQuestion <- stringField(form, "openQuestion").right
      rating <- intField(form, "rating", min = 1, max = 5).right
    } yield GameInput(owner, email, strong, weak, important, hard, inspiring, tedious, topaasia, openQuestion, rating)

  def fromInput(game: GameInput, createdAt: LocalDateTime, cards: Card.Collection): Game = {
    def card(ci: CardGradeInput): CardGrade = {
      val c = cards.get(ci.code)
      CardGrade(
        code = ci.code,
        subject = c.map(_.subject).getOrElse(ci.code),
        sentence = c.map(_.sentence).getOrElse(ci.code),
        grade = ci.grade)
    }

    Game(
      owner = game.owner,
      email = game.email,
      strong = card(game.strong),
      weak = card(game.weak),
      important = card(game.important),
      hard = card(game.hard),
      tedious = card(game.tedious),
      inspiring = card(game.inspiring),
      topaasia = game.topaasia,
      openQuestion = game.openQuestion,
      rating = game.rating,
      createdAt = createdAt
    )
  }

  private def cardFromForm(form: UrlForm, level: String) = for {
    card <- cardName(form, s"${level}Card").right
    grade <- intField(form, s"${level}Num", min = 1, max = 4).right
  } yield CardGradeInput(card, grade)

  private def cardName(form: UrlForm, field: String) =
    stringField(form, field)
      .right.map(_.toUpperCase)
      .right.flatMap(filterCardNumber)

  private def filterCardNumber(s: String): Either[String, String] =
    trailingNumber
      .findFirstIn(s)
      .flatMap(stringToInt)
      .filter(n => n >= minCardNumber && n <= maxCardNumber)
      .map(_ => s)
      .toRight(s"Card name '$s' should end with a number between $minCardNumber-$maxCardNumber.")
}

final case class GameInput(
  owner: String,
  email: String,
  strong: CardGradeInput,
  weak: CardGradeInput,
  important: CardGradeInput,
  hard: CardGradeInput,
  tedious: CardGradeInput,
  inspiring: CardGradeInput,
  topaasia: String,
  openQuestion: String,
  rating: Int
) {

  val cardGrades = List(strong, weak, important, hard, tedious, inspiring)

  val codes: Set[Card.Code] = cardGrades.map(_.code).toSet
}

final case class CardGradeInput(code: Card.Code, grade: Int)

final case class Game(
  owner: String,
  email: String,
  strong: CardGrade,
  weak: CardGrade,
  important: CardGrade,
  hard: CardGrade,
  tedious: CardGrade,
  inspiring: CardGrade,
  topaasia: String,
  openQuestion: String,
  rating: Int,
  createdAt: LocalDateTime
)

final case class CardGrade(code: Card.Code, subject: String, sentence: String, grade: Int) {
  def render: String = s"$subject ($grade)"
}