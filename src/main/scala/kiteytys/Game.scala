package kiteytys

import java.time.LocalDateTime

import org.http4s.UrlForm
import scala.util.Try

object Game {
  import FormParsing._

  def fromForm(form: UrlForm): Either[String, GameInput] = {
    import CardGradeInput.{fromForm => cardFromForm}

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
  }

  def fromInput(game: GameInput, createdAt: LocalDateTime, cards: Card.Collection): Game =
    Game(
      owner = game.owner,
      email = game.email,
      strong = cards.graded(game.strong),
      weak = cards.graded(game.weak),
      important = cards.graded(game.important),
      hard = cards.graded(game.hard),
      tedious = cards.graded(game.tedious),
      inspiring = cards.graded(game.inspiring),
      topaasia = cards(game.topaasia),
      topaasiaAnswer = game.topaasiaAnswer,
      rating = game.rating,
      createdAt = createdAt
    )
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
  topaasia: Card.Code,
  topaasiaAnswer: String,
  rating: Int) {

  val cardGrades = List(strong, weak, important, hard, tedious, inspiring)

  val codes: Set[Card.Code] = cardGrades.map(_.code).toSet
}

final case class Game(
  owner: String,
  email: String,
  strong: CardGrade,
  weak: CardGrade,
  important: CardGrade,
  hard: CardGrade,
  tedious: CardGrade,
  inspiring: CardGrade,
  topaasia: Card,
  topaasiaAnswer: String,
  rating: Int,
  createdAt: LocalDateTime
)
