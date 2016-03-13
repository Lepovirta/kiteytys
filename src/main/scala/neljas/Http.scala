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
  extends TwirlInstances with Conversions with LazyLogging {

  private val static = cachedResource(Config("/static", "/static"))

  val rootService = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root / "ping" =>
      Ok("pong")

    case GET -> Root =>
      Ok(html.index.render())

    case req @ POST -> Root / "submit" =>
      parseForm(req, User.fromForm) { user =>
        val page = html.pdf.render(user).toString()
        val result = for {
          bytes <- pdf.generate(page)
          _ <- pdf.save(bytes)
          _ <- mailer.sendPDF(user, bytes)
        } yield bytes
        Ok(result).withContentType(Some(`Content-Type`(`application/pdf`)))
      }
  }

  val messageService = HttpService {
    case GET -> Root =>
      val result = repos.message.fetchAll
      Ok(result)

    case req @ POST -> Root =>
      req.decode[Message] { message =>
        val result = repos.message.create(message).map(_.toString)
        Ok(result)
      }

    case GET -> Root / LongVar(id) =>
      repos.message.fetchById(id).flatMap {
        case Some(msg) => Ok(msg)
        case None => NotFound(s"Could not found message #$id")
      }
  }

  val service = Router(
    "" -> rootService,
    "/message" -> messageService
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
