package neljas

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.http4s.server.blaze.BlazeBuilder

object Main extends App with LazyLogging {
  val port = Option(System.getenv("PORT")).map(_.toInt).getOrElse(8080)

  logger.info("Starting server in port: {}", port.toString)

  BlazeBuilder
    .bindHttp(port)
    .mountService(Http.route, "/")
    .run
    .awaitShutdown()
}