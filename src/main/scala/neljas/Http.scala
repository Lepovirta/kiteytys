package neljas

import com.typesafe.config._
import org.http4s.headers.{`Content-Type`}
import org.http4s.MediaType._
import org.http4s.{UrlForm, HttpService}
import org.http4s.dsl._
import org.http4s.twirl.TwirlInstances
import org.http4s.server.staticcontent
import org.http4s.server.staticcontent.ResourceService.Config

import neljas.pdf.PDF

object Http extends TwirlInstances {

  private val conf   = ConfigFactory.load()
  private val static = cachedResource(Config("/static", "/static"))

  val route = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root / "conf" =>
      Ok(conf.getString("smtp.port"))

    case GET -> Root =>
      Ok(html.index.render())

    case req @ POST -> Root / "submit" =>
      req.decode[UrlForm] { form =>
        User.fromForm(form) match {
          case Left(errors) => BadRequest(errors)
          case Right(user) =>
            val page = html.pdf.render(user).toString()
            val pdf = PDF.generate(page)
            Ok(pdf).withContentType(Some(`Content-Type`(`application/pdf`)))
        }
      }
  }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
