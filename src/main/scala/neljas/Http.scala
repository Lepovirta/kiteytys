package neljas

import org.http4s.headers.{`Content-Type`}
import org.http4s.MediaType._
import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.twirl.TwirlInstances

import neljas.pdf.PDF

object Http extends TwirlInstances {

  val route = HttpService {
    case GET -> Root / "ping" =>
      Ok("pong")

    case GET -> Root =>
      Ok(html.index.render())

    case POST -> Root / "submit" =>
      // TODO: parse from params
      val u = User("matti", 3)
      // TODO: pass user object
      val page = html.result.render(u.name, u.age).toString()
      val pdf = PDF.generate(page)
      Ok(pdf).withContentType(Some(`Content-Type`(`application/pdf`)))
  }
}
