package neljas.db

import doobie.imports._
import neljas.Game
import scalaz.concurrent.Task

class GameRepository(xa: Transactor[Task]) extends Repository {

  private val ddl: Update0 =
    sql"""
      CREATE TABLE IF NOT EXISTS game (
        id            IDENTITY,
        identifier    VARCHAR NOT NULL,
        email         VARCHAR NOT NULL,
        strongCard    VARCHAR NOT NULL,
        strongNum     INT NOT NULL,
        weakCard      ARCHAR NOT NULL,
        weakNum       INT NOT NULL,
        importantCard VARCHAR NOT NULL,
        importantNum  INT NOT NULL,
        hardCard      VARCHAR NOT NULL,
        hardNum       INT NOT NULL,
        tediousCard   VARCHAR NOT NULL,
        tediousNum    INT NOT NULL,
        inspiringCard VARCHAR NOT NULL,
        inspiringNum  INT NOT NULL,
        topaasia      VARCHAR NOT NULL,
        openQuestion  VARCHAR NOT NULL,
        rating        INT NOT NULL
      )
    """.update

  private def sqlCreate(g: Game): Update0 =
    sql"""
      INSERT INTO game (
        identifier, email, strongCard, strongNum, weakCard, weakNum,
        importantCard, importantNum, hardCard, hardNum, tediousCard,
        tediousNum, inspiringCard, inspiringNum, topaasia, openQuestion, rating
      ) VALUES (
        ${g.identifier}, ${g.email}, ${g.strong.name}, ${g.strong.grade},
        ${g.weak.name}, ${g.weak.grade}, ${g.important.name}, ${g.important.grade},
        ${g.hard.name}, ${g.hard.grade}, ${g.tedious.name}, ${g.tedious.grade},
        ${g.inspiring.name}, ${g.inspiring.grade}, ${g.topaasia},
        ${g.openQuestion}, ${g.rating}
      )
    """.update

  override def initSchema: Task[Unit] =
    ddl.run.transact(xa).map(_ => {})

  def create(message: Game): Task[Long] =
    sqlCreate(message).withUniqueGeneratedKeys[Long]("id").transact(xa)
}
