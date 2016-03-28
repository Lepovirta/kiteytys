package kiteytys

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.http4s.UrlForm

object Game {
  import FormParsing._

  def fromForm(form: UrlForm): Either[Error, GameInput] = {
    import CardGradeInput.{fromFormOptional => cardFromForm}
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
      topaasia <- TopaasiaInput.fromFormOptional(form).right
      rating <- intField(form, "rating", min = 1, max = 5).right
    } yield GameInput(owner, email, strong, weak, important, hard, inspiring, tedious, topaasia, rating)
  }

  def fromInput(game: GameInput, owner: Owner, createdAt: LocalDateTime, cards: Card.Collection): Game =
    Game(
      owner = owner,
      email = game.email,
      strong = game.strong.map(cards.graded),
      weak = game.weak.map(cards.graded),
      important = game.important.map(cards.graded),
      hard = game.hard.map(cards.graded),
      tedious = game.tedious.map(cards.graded),
      inspiring = game.inspiring.map(cards.graded),
      topaasia = game.topaasia.map(t => Topaasia(cards(t.code), t.answer)),
      rating = game.rating,
      createdAt = createdAt
    )
}

final case class GameInput(
  owner: String,
  email: String,
  strong: Option[CardGradeInput],
  weak: Option[CardGradeInput],
  important: Option[CardGradeInput],
  hard: Option[CardGradeInput],
  tedious: Option[CardGradeInput],
  inspiring: Option[CardGradeInput],
  topaasia: Option[TopaasiaInput],
  rating: Int) {

  val cardGrades = List(strong, weak, important, hard, tedious, inspiring)

  val codes: Set[Card.Code] = (cardGrades.flatMap(_.map(_.code)) ++ topaasia.map(_.code)).toSet
}

final case class Game(
  owner: Owner,
  email: String,
  strong: Option[CardGrade],
  weak: Option[CardGrade],
  important: Option[CardGrade],
  hard: Option[CardGrade],
  tedious: Option[CardGrade],
  inspiring: Option[CardGrade],
  topaasia: Option[Topaasia],
  rating: Int,
  createdAt: LocalDateTime) {

  val cardGrades = List(strong, weak, important, hard, tedious, inspiring).flatten.sorted

  def renderDate: String = {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    createdAt.format(formatter)
  }
}

final case class Owner(id: String, name: String) {
  def render: String = s"$id ($name)"
}
