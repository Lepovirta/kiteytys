package kiteytys.db

import doobie.imports._
import kiteytys.Errors.NoOwnerFound
import kiteytys.Owner

import scalaz.concurrent.Task

object OwnerRepository extends DoobieImplicits {
  def sqlFetchById(id: String): Query0[Owner] =
    sql"""
        SELECT id, name
        FROM owner
        WHERE id = $id
      """.query[Owner]
}

class OwnerRepository(xa: Transactor[Task]) extends Repository {
  import OwnerRepository._

  def fetchById(id: String): Task[Owner] =
    sqlFetchById(id).option.transact(xa).flatMap {
      case Some(owner) => Task.now(owner)
      case None => Task.fail(new NoOwnerFound(id))
    }
}
