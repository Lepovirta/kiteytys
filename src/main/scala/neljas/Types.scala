package neljas

import org.http4s.UrlForm

import scala.util.Try
import scalaz.Scalaz._

object User {
  def fromForm(form: UrlForm): Either[String, User] = {
    val result = for {
      name <- form.getFirst("name").toSuccess("Missing 'name'")
      ageString <- form.getFirst("age").toSuccess("Missing 'age'")
      age <- Try(ageString.toInt).toOption.toSuccess("Invalid format for field 'age'")
    } yield User(name, age)

    result.toEither
  }
}

final case class User(name: String, age: Int)
