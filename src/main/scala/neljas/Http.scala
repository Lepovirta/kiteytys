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

import scalaz.concurrent.Task


final class Http(repos: Repositories, mailer: Mailer, pdf: PDF)
  extends TwirlInstances with LazyLogging {

  private val static = cachedResource(Config("/static", "/static"))

  val rootService = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root =>
      Ok(html.index.render())

    case req @ POST -> Root / "submit" =>
      parseForm(req, Game.fromForm) { game =>
        val page = html.pdf.render(game).toString()
        val result = for {
          bytes <- pdf.generate(page)
          _ <- pdf.save(bytes)
          _ <- mailer.sendPDF(game.email, bytes)
        } yield bytes
        Ok(result).withContentType(Some(`Content-Type`(`application/pdf`)))
      }
  }

  val service = Router(
    "" -> rootService
  )

  private def parseForm[A](req: Request, parse: UrlForm => Either[String, A])(f: A => Task[Response]): Task[Response] =
    req.decode[UrlForm] { form =>
      parse(form) match {
        case Left(errors) => BadRequest(errors)
        case Right(model) => f(model)
      }
    }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
