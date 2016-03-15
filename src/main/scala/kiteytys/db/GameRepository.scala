package kiteytys.db

import java.time.LocalDateTime

import doobie.imports._
import kiteytys.GameInput
import scalaz.concurrent.Task

object GameRepository extends DoobieImplicits {

  def sqlCreate(game: GameInput, createdAt: LocalDateTime): Update0 =
      sql"""
        INSERT INTO game (
          owner, email, createdAt,
          strongCard, strongNum, weakCard, weakNum,
          importantCard, importantNum, hardCard, hardNum,
          tediousCard, tediousNum, inspiringCard, inspiringNum,
          topaasia, openQuestion, rating
        ) VALUES (
          ${game.owner}, ${game.email}, $createdAt,
          ${game.strong.code}, ${game.strong.grade}, ${game.weak.code}, ${game.weak.grade},
          ${game.important.code}, ${game.important.grade}, ${game.hard.code}, ${game.hard.grade},
          ${game.tedious.code}, ${game.tedious.grade}, ${game.inspiring.code}, ${game.inspiring.grade},
          ${game.topaasia}, ${game.openQuestion}, ${game.rating}
        )
      """.update
}

class GameRepository(xa: Transactor[Task]) extends Repository {

  import GameRepository._

  def create(game: GameInput, dt: LocalDateTime): Task[Long] =
    sqlCreate(game, dt).withUniqueGeneratedKeys[Long]("id").transact(xa)
}
