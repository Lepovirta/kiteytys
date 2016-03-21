package kiteytys.db

import doobie.imports._
import kiteytys.Card
import scalaz.NonEmptyList
import scalaz.concurrent.Task
import scalaz.Scalaz._

object CardRepository extends DoobieImplicits {

  val sqlCreate: Update[Card] =
    Update[Card]("INSERT INTO card (code, subject, sentence) VALUES (?, ?, ?)")

  def sqlFetchByCodes(codes: NonEmptyList[Card.Code]): Query0[Card] = {
    implicit val codesParam = Param.many(codes)
    sql"""
      SELECT code, subject, sentence
      FROM card
      WHERE code IN (${codes: codes.type})
    """.query[Card]
  }
}

class CardRepository(xa: Transactor[Task]) extends Repository {

  import CardRepository._

  def create(card: Card): Task[Card] =
    sqlCreate.withUniqueGeneratedKeys[Card]("code", "subject", "sentence")(card).transact(xa)

  def fetchByCodes(codes: Set[Card.Code]): Task[List[Card]] =
    codes.toList
      .map(_.toUpperCase)
      .toNel
      .map(codesList => sqlFetchByCodes(codesList).list.transact(xa))
      .getOrElse(Task.now(Nil))
}
