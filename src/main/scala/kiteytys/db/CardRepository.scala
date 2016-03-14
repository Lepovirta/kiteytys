package kiteytys.db

import doobie.imports._
import kiteytys.Card
import scalaz.concurrent.Task

class CardRepository(xa: Transactor[Task]) extends Repository {

  private def sqlCreate(c: Card): Update0 =
    sql"""
      INSERT INTO card (code, subject, sentence)
      VALUES (${c.code}, ${c.subject}, ${c.sentence})
    """.update

  def create(card: Card): Task[Card] =
    sqlCreate(card).withUniqueGeneratedKeys[Card]("code", "subject", "sentence").transact(xa)
}
