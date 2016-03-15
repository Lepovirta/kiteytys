package kiteytys

import org.http4s.UrlForm

object Card {
  type Code = String

  case class Collection(cards: Map[Code, Card]) extends AnyVal {
    def get(code: Code): Option[Card] = cards.get(code)
    def apply(code: Code): Card = cards.getOrElse(code, Card(code, code, code))
    def graded(input: CardGradeInput): CardGrade = CardGrade(apply(input.code), input.grade)
  }

  val minCardNumber = 1
  val maxCardNumber = 52

  def collectionFromIterable(iterable: Iterable[Card]): Collection =
    Collection(iterable.groupBy(_.code).mapValues(_.head))
}

final case class Card(
  code: Card.Code,
  subject: String,
  sentence: String
)

object CardGradeInput {
  import FormParsing._

  private val trailingNumber = "\\d+$".r

  def fromForm(form: UrlForm, level: String) = for {
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
      .filter(n => n >= Card.minCardNumber && n <= Card.maxCardNumber)
      .map(_ => s)
      .toRight(s"Card name '$s' should end with a number between ${Card.minCardNumber}-${Card.maxCardNumber}.")
}

final case class CardGradeInput(code: Card.Code, grade: Int)

object CardGrade {
  def apply(card: Card, grade: Int): CardGrade = CardGrade(card.code, card.subject, card.sentence, grade)
}

final case class CardGrade(code: Card.Code, subject: String, sentence: String, grade: Int) {
  def render: String = s"$subject ($grade)"
}