package typedapi.internal.test.util

import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.generic.JsonCodec
import cats.effect.IO

@JsonCodec final case class User(name: String, age: Int)

object UserCoding {

  implicit val decoder = jsonOf[IO, User]
  implicit val encoder = jsonEncoderOf[IO, User]
}
