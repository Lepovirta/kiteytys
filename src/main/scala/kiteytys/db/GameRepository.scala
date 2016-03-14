package kiteytys.db

import doobie.imports._
import kiteytys.Game
import scalaz.concurrent.Task

class GameRepository(xa: Transactor[Task]) extends Repository {

  private def sqlCreate(g: Game): Update0 =
    sql"""
      INSERT INTO game (
        owner, email, strongCard, strongNum, weakCard, weakNum,
        importantCard, importantNum, hardCard, hardNum, tediousCard,
        tediousNum, inspiringCard, inspiringNum, topaasia, openQuestion, rating
      ) VALUES (
        ${g.owner}, ${g.email}, ${g.strong.name}, ${g.strong.grade},
        ${g.weak.name}, ${g.weak.grade}, ${g.important.name}, ${g.important.grade},
        ${g.hard.name}, ${g.hard.grade}, ${g.tedious.name}, ${g.tedious.grade},
        ${g.inspiring.name}, ${g.inspiring.grade}, ${g.topaasia},
        ${g.openQuestion}, ${g.rating}
      )
    """.update

  def create(message: Game): Task[Long] =
    sqlCreate(message).withUniqueGeneratedKeys[Long]("id").transact(xa)
}
