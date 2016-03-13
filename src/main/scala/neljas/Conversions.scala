package neljas

import argonaut.EncodeJson
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.argonaut.ArgonautInstances

trait Conversions extends ArgonautInstances {
  implicit def vectorEntityEncoder[A: EncodeJson]: EntityEncoder[Vector[A]] = jsonEncoderOf[Vector[A]]
  implicit def listEntityEncoder[A: EncodeJson]: EntityEncoder[List[A]] = jsonEncoderOf[List[A]]
  implicit val messageEntityEncoder: EntityEncoder[Message] = jsonEncoderOf[Message]
  implicit val messageEntityDecoder: EntityDecoder[Message] = jsonOf[Message]
}
