package neljas

import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.twirl.TwirlInstances

object Http extends TwirlInstances {

  val route = HttpService {
    case GET -> Root / "ping" =>
      Ok("pong")

    case GET -> Root =>
      Ok(html.index.render())

    case POST -> Root / "submit" =>
      Ok("form was submitted!")
  }
}
