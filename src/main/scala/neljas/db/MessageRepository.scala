package neljas.db

import doobie.imports._
import neljas.Message
import scalaz.concurrent.Task
import scalaz.stream.Process

class MessageRepository(xa: Transactor[Task]) extends Repository {

  private val ddl: Update0 =
    sql"""
      CREATE TABLE IF NOT EXISTS message (
        id IDENTITY,
        title VARCHAR NOT NULL,
        content VARCHAR NOT NULL
      )
    """.update

  private def sqlCreate(message: Message): Update0 =
    sql"""
      INSERT INTO message (title, content) VALUES (${message.title}, ${message.content})
    """.update

  private def sqlFetchById(id: Long): Query0[Message] =
    sql"""
      SELECT title, content FROM MESSAGE
      WHERE id = $id
    """.query[Message]

  private def sqlFetchAll: Query0[Message] =
    sql"""
      SELECT title, content FROM MESSAGE
    """.query[Message]

  override def initSchema: Task[Unit] =
    ddl.run.transact(xa).map(_ => {})

  def create(message: Message): Task[Long] =
    sqlCreate(message).withUniqueGeneratedKeys[Long]("id").transact(xa)

  def fetchById(id: Long): Task[Option[Message]] =
    sqlFetchById(id).option.transact(xa)

  def fetchAll: Task[List[Message]] =
    sqlFetchAll.list.transact(xa)
}
