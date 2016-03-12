package neljas

import org.http4s.headers.{`Content-Type`}
import org.http4s.MediaType._
import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.twirl.TwirlInstances
import org.http4s.server.staticcontent
import org.http4s.server.staticcontent.ResourceService.Config

import neljas.pdf.PDF

object Http extends TwirlInstances {

  private val static = cachedResource(Config("/static", "/static"))

  val route = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root / "ping" =>
      Ok("pong")

    case GET -> Root =>
      Ok(html.index.render())

    case POST -> Root / "submit" =>
      // TODO: parse from params
      val u = User("matti", 3)
      // TODO: pass user object
      val page = html.pdf.render(u.name, u.age).toString()
      val pdf = PDF.generate(page)
      Ok(pdf).withContentType(Some(`Content-Type`(`application/pdf`)))
  }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
