package neljas

import com.typesafe.config._
import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.http4s.server.blaze.BlazeBuilder

import neljas.conf.Conf

object Main extends App with LazyLogging {
  if (args.length < 1) {
    throw new Exception("Configuration file argument missing.")
  }

  val conf = Conf.load(Main.args(0))
  val port = conf.port

  logger.info("Starting server in port: {}", port.toString)

  BlazeBuilder
    .bindHttp(port)
    .mountService(Http(conf).route, "/")
    .run
    .awaitShutdown()
}
