package dtos

import zio.json._
import java.util.UUID

case class TrackerUser(id: UUID, email: String, auth0Id: String)

object TrackerUser {
  implicit val decoder: JsonDecoder[TrackerUser] = DeriveJsonDecoder.gen[TrackerUser]
  implicit val encoder: JsonEncoder[TrackerUser] = DeriveJsonEncoder.gen[TrackerUser]
}
