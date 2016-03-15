package kiteytys

import java.time.LocalDateTime

import org.http4s.UrlForm

object Game {
  import FormParsing._

  def fromForm(form: UrlForm): Either[String, GameInput] = {
    import CardGradeInput.{fromForm => cardFromForm}
    import CardLevel._

    for {
      owner <- stringField(form, "owner").right
      email <- stringField(form, "email").right
      strong <- cardFromForm(form, Strong).right
      weak <- cardFromForm(form, Weak).right
      important <- cardFromForm(form, Important).right
      hard <- cardFromForm(form, Hard).right
      tedious <- cardFromForm(form, Tedious).right
      inspiring <- cardFromForm(form, Inspiring).right
      topaasia <- stringField(form, "topaasia").right
      openQuestion <- stringField(form, "openQuestion").right
      rating <- intField(form, "rating", min = 1, max = 5).right
    } yield GameInput(owner, email, strong, weak, important, hard, inspiring, tedious, topaasia, openQuestion, rating)
  }

  def fromInput(game: GameInput, owner: Owner, createdAt: LocalDateTime, cards: Card.Collection): Game =
    Game(
      owner = owner,
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
  owner: Owner,
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
  createdAt: LocalDateTime) {

  val cardGrades = List(strong, weak, important, hard, tedious, inspiring).sorted
}

final case class Owner(id: String, name: String)