package neljas

import com.typesafe.scalalogging.LazyLogging
import neljas.db.Repositories
import neljas.email.Mailer
import neljas.pdf.PDF
import org.http4s.MediaType._
import org.http4s._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.server.staticcontent.ResourceService.Config
import org.http4s.server.{Router, staticcontent}
import org.http4s.twirl.TwirlInstances

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task


final class Http(repos: Repositories, mailer: Mailer, pdf: PDF)
  extends TwirlInstances with LazyLogging {

  private val static = cachedResource(Config("/static", "/static"))

  val rootService = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root =>
      Ok(html.index())

    case req @ POST -> Root / "submit" =>
      req.decode[UrlForm] { form =>
        val result = for {
          game <- gameDataFromForm(form)
          bytes <- pdf.generate(html.pdf.render(game).toString)
          _ <- pdf.save(bytes)
          _ <- mailer.sendPDF(game.email, bytes)
        } yield bytes

        result.attempt.flatMap {
          case \/-(bytes) =>
            Ok(bytes).withContentType(Some(`Content-Type`(`application/pdf`)))
          case -\/(err) =>
            BadRequest(html.index(form, Some(err.getMessage)))
        }
      }

    case _ =>
      NotFound(html.notFound.render())
  }

  val service = Router(
    "" -> rootService
  )

  private def gameDataFromForm(form: UrlForm): Task[Game] =
    Game.fromForm(form) match {
      case Left(errors) => Task.fail(new RuntimeException(errors))
      case Right(game) => Task.now(game)
    }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
