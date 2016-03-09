package neljas

import org.http4s.HttpService
import org.http4s.dsl._

object Http {

  val route = HttpService {
    case GET -> Root / "ping" =>
      Ok("pong")
  }
}
