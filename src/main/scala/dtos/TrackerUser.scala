package dtos

import zio.json._

case class TrackerUser(id: Int, name: String)

object TrackerUser {
  implicit val decoder: JsonDecoder[TrackerUser] = DeriveJsonDecoder.gen[TrackerUser]
  implicit val encoder: JsonEncoder[TrackerUser] = DeriveJsonEncoder.gen[TrackerUser]
}
