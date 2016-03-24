package kiteytys

import org.http4s.UrlForm

object Card {
  type Code = String

  case class Collection(cards: Map[Code, Card]) extends AnyVal {
    def get(code: Code): Option[Card] = cards.get(code)
    def apply(code: Code): Card = cards.getOrElse(code, Card(code = code, subject = code, sentence = ""))
    def graded(input: CardGradeInput): CardGrade = CardGrade(apply(input.code), input.grade, input.level)
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

  def fromForm(form: UrlForm, level: CardLevel): Either[Error, CardGradeInput] = for {
    card <- stringField(form, s"${level.stringId}Card").right
    grade <- intField(form, s"${level.stringId}Num", min = 1, max = 4).right
  } yield CardGradeInput(card, grade, level)
}

final case class CardGradeInput(code: Card.Code, grade: Int, level: CardLevel)

object CardGrade {
  def apply(card: Card, grade: Int, level: CardLevel): CardGrade =
    CardGrade(card.code, card.subject, card.sentence, grade, level)

  implicit val cardGradeOrdering: Ordering[CardGrade] = Ordering.by(_.level)
}

final case class CardGrade(code: Card.Code, subject: String, sentence: String, grade: Int, level: CardLevel) {
  def render: String = s"$subject ($grade)"
}

sealed trait CardLevel {
  def stringId: String
  def text: String
}

object CardLevel extends Iterable[CardLevel] {
  case object Strong extends CardLevel {
    def stringId: String = "strong"
    def text: String = "Vahvin"
  }

  case object Weak extends CardLevel {
    def stringId: String = "weak"
    def text: String = "Heikoin"
  }

  case object Important extends CardLevel {
    def stringId: String = "important"
    def text: String = "Tärkein"
  }

  case object Hard extends CardLevel {
    def stringId: String = "hard"
    def text: String = "Vaikein"
  }

  case object Tedious extends CardLevel {
    def stringId: String = "tedious"
    def text: String = "Ikävin"
  }

  case object Inspiring extends CardLevel {
    def stringId: String = "inspiring"
    def text: String = "Innostavin"
  }

  def fromString(s: String): Option[CardLevel] =
    list.find(_.stringId == s)

  def fromStringEither(s: String): Either[String, CardLevel] =
    fromString(s).toRight(s"Unknown card level ID '$s'")

  val list = List(Strong, Weak, Important, Hard, Tedious, Inspiring)

  private val ordered: Map[CardLevel, Int] = list.zipWithIndex.toMap

  val values = list.toSet

  override def iterator: Iterator[CardLevel] = list.iterator

  implicit val cardLevelOrdering: Ordering[CardLevel] = Ordering.by(ordered.get)
}
