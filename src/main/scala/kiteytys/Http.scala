package kiteytys

import com.typesafe.scalalogging.LazyLogging
import kiteytys.Errors.{FormParsingError, UserVisibleError}
import org.http4s.MediaType._
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.server.staticcontent.ResourceService.Config
import org.http4s.server.{Router, staticcontent}
import org.http4s.twirl.TwirlInstances

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}


final class Http(gameCreator: GameCreator)
  extends TwirlInstances with LazyLogging {

  private val static = cachedResource(Config("/static", "/static"))

  val rootService = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root =>
      Ok(html.index())

    case req @ POST -> Root / "submit" =>
      req.decode[UrlForm] { form =>
        val pdfBytes = gameDataFromForm(form).flatMap(gameCreator.create)

        pdfBytes.attempt.flatMap {
          case \/-(bytes) =>
            Ok(bytes).withContentType(Some(`Content-Type`(`application/pdf`)))
          case -\/(err) =>
            logger.error("Error occurred while attempting to save a new game", err)
            BadRequest(html.index(form, Some(getErrorMessage(err))))
        }
      }

    case _ =>
      NotFound(html.notFound.render())
  }

  val service = Router(
    "" -> rootService
  )

  private def getErrorMessage(t: Throwable): String = t match {
    case ex: UserVisibleError => ex.userVisibleMessage
    case _ => "Lomakkeen lähettäminen epäonnistui odottamattomasti."
  }

  private def gameDataFromForm(form: UrlForm): Task[GameInput] =
    Game.fromForm(form) match {
      case Left(errors) => Task.fail(new FormParsingError(errors, FormParsing.gameFieldToFinnish))
      case Right(game) => Task.now(game)
    }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
