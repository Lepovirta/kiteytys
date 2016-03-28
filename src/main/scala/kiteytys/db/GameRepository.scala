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
          ${game.strong.map(_.code)}, ${game.strong.map(_.grade)}, ${game.weak.map(_.code)}, ${game.weak.map(_.grade)},
          ${game.important.map(_.code)}, ${game.important.map(_.grade)}, ${game.hard.map(_.code)}, ${game.hard.map(_.grade)},
          ${game.tedious.map(_.code)}, ${game.tedious.map(_.grade)}, ${game.inspiring.map(_.code)}, ${game.inspiring.map(_.grade)},
          ${game.topaasia.map(_.card.code)}, ${game.topaasia.map(_.answer)}, ${game.rating}
        )
      """.update
}

class GameRepository(xa: Transactor[Task]) extends Repository {

  import GameRepository._

  def create(game: Game): Task[Long] =
    sqlCreate(game).withUniqueGeneratedKeys[Long]("id").transact(xa)
}
