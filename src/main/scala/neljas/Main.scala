package neljas

import com.typesafe.scalalogging.LazyLogging
import neljas.conf.Conf
import org.http4s.server.blaze.BlazeBuilder

object Main extends App with LazyLogging {
  if (args.length < 1) {
    throw new Exception("Configuration file argument missing.")
  }

  // Setup components
  val conf = Conf.load(Main.args(0))
  val database = new db.Database(conf.database)
  val http = BlazeBuilder
    .bindHttp(conf.port)
    .mountService(new Http(conf, database.repos).service, "/")

  private def startHttp(): Unit = {
    logger.info("Starting server in port: {}", conf.port.toString)

    http
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
