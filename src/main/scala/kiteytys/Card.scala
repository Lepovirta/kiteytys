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

  private def cardField(level: CardLevel) = s"${level.stringId}Card"
  private def gradeField(level: CardLevel) = s"${level.stringId}Num"

  def fromFormOptional(form: UrlForm, level: CardLevel): Either[Error, Option[CardGradeInput]] =
    stringInputs(form, level) match {
      case Some((card, gradeString)) => inputsToCardGrade(level, card, gradeString)
      case None => Right(None)
    }

  private def stringInputs(form: UrlForm, level: CardLevel) = for {
    card <- form.getFirst(cardField(level)).flatMap(nonEmptyString)
    grade <- form.getFirst(gradeField(level)).flatMap(nonEmptyString)
  } yield (card, grade)

  private def inputsToCardGrade(level: CardLevel, card: String, gradeString: String) =
    stringToValidInt(gradeField(level), gradeString, min = 1, max = 4)
      .right.map(grade => Some(CardGradeInput(card, grade, level)))
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

object TopaasiaInput {
  import FormParsing._

  def fromFormOptional(form: UrlForm): Either[Error, Option[TopaasiaInput]] = {
    val result = for {
      code <- form.getFirst("topaasia").flatMap(nonEmptyString)
      answer <- form.getFirst("topaasiaAnswer")
    } yield TopaasiaInput(code, answer)
    Right(result)
  }
}

final case class TopaasiaInput(code: Card.Code, answer: String)

final case class Topaasia(card: Card, answer: String)

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
