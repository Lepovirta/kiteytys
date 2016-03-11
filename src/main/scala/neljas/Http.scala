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
      // TODO: parse from params
      val u = new User("matti", 3)
      // TODO: pass user object
      Ok(html.result.render(u.name, u.age))
  }
}
