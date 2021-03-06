package kiteytys.db

import java.sql.{Connection, Timestamp}
import java.time.LocalDateTime

import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.imports.{Capture, Meta, Transactor}
import kiteytys.conf.Conf
import org.flywaydb.core.Flyway

import scalaz.concurrent.Task
import scalaz.{Catchable, Monad}

final class Database(conf: Conf.Database) extends LazyLogging {
  val hikariConfig = {
    val config = new HikariConfig()
    config.setDriverClassName(conf.driver)
    config.setJdbcUrl(conf.url)
    config.setUsername(conf.username)
    config.setPassword(conf.password)
    config
  }

  val dataSource = new HikariDataSource(hikariConfig)

  private val flyway = {
    val fw = new Flyway
    fw.setDataSource(dataSource)
    fw
  }

  val xa: Transactor[Task] = new HikariTransactor[Task](dataSource)

  val repos = new Repositories(xa)

  def close(): Unit = {
    logger.info("Closing database data source")
    dataSource.close()
  }

  def init(): Unit = {
    logger.info("Initializing database")
    val result = flyway.migrate()
    logger.info(s"Successfully applied $result migrations.")
  }
}

final class Repositories(xa: Transactor[Task]) {
  val game = new GameRepository(xa)
  val card = new CardRepository(xa)
  val owner = new OwnerRepository(xa)
}

trait DoobieImplicits {
  implicit val LocalTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].xmap(_.toLocalDateTime, Timestamp.valueOf)
}

trait Repository {
}

protected final class HikariTransactor[M[_]: Monad : Catchable : Capture](ds: HikariDataSource) extends Transactor {
  override protected def connect: M[Connection] = Capture[M].apply(ds.getConnection)
}

