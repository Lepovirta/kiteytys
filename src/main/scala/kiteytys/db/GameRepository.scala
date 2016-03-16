package kiteytys.db

import doobie.imports._
import kiteytys.Game

import scalaz.concurrent.Task

object GameRepository extends DoobieImplicits {

  def sqlCreate(game: Game): Update0 =
      sql"""
        INSERT INTO game (
          owner, email, createdAt,
          strongCard, strongNum, weakCard, weakNum,
          importantCard, importantNum, hardCard, hardNum,
          tediousCard, tediousNum, inspiringCard, inspiringNum,
          topaasia, topaasiaAnswer, rating
        ) VALUES (
          ${game.owner.id}, ${game.email}, ${game.createdAt},
          ${game.strong.code}, ${game.strong.grade}, ${game.weak.code}, ${game.weak.grade},
          ${game.important.code}, ${game.important.grade}, ${game.hard.code}, ${game.hard.grade},
          ${game.tedious.code}, ${game.tedious.grade}, ${game.inspiring.code}, ${game.inspiring.grade},
          ${game.topaasia.code}, ${game.topaasiaAnswer}, ${game.rating}
        )
      """.update
}

class GameRepository(xa: Transactor[Task]) extends Repository {

  import GameRepository._

  def create(game: Game): Task[Long] =
    sqlCreate(game).withUniqueGeneratedKeys[Long]("id").transact(xa)
}
