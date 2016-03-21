package kiteytys

import com.typesafe.scalalogging.LazyLogging
import kiteytys.conf.Conf
import kiteytys.email.Mailer
import kiteytys.pdf.PDF
import org.http4s.server.blaze.BlazeBuilder

object Main extends App with LazyLogging {
  if (args.length < 1) {
    throw new Exception("Configuration file argument missing.")
  }

  // Setup components
  val conf = Conf.load(Main.args(0))
  val database = new db.Database(conf.database)
  val mailer = new Mailer(conf.email)
  val pdf = new PDF(conf.pdf)
  val gameCreator = new GameCreator(database.repos, mailer, pdf)
  val http = new Http(gameCreator)

  private def startHttp(): Unit = {
    logger.info("Starting server in port: {}", conf.http.port.toString)

    BlazeBuilder
      .bindHttp(conf.http.port)
      .mountService(http.service, "/")
      .run
      .onShutdown(shutdownHook())
      .awaitShutdown()
  }

  private def shutdownHook(): Unit = {
    database.close()
  }

  // Initialize components
  database.init()
  startHttp()
}
