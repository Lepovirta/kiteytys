package kiteytys

object Card {
  type Code = String
  type Collection = Map[Code, Card]

  def collectionFromIterable(iterable: Iterable[Card]): Collection =
    iterable.groupBy(_.code).mapValues(_.head)
}

final case class Card(
  code: Card.Code,
  subject: String,
  sentence: String
)
