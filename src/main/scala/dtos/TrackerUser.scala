package dtos

import zio.json._
import java.util.UUID
import zio.test.Gen

case class TrackerUser(id: UUID, email: String, auth0Id: String)

object TrackerUser {
  implicit val decoder: JsonDecoder[TrackerUser] = DeriveJsonDecoder.gen[TrackerUser]
  implicit val encoder: JsonEncoder[TrackerUser] = DeriveJsonEncoder.gen[TrackerUser]

  val genUserWithAuth0Id = (auth0Id: String) =>
    for {
      id    <- Gen.anyUUID
      email <- Gen.anyString
    } yield TrackerUser(id, email, auth0Id)

  val genUser = for {
    auth0Id <- Gen.anyString
    user    <- genUserWithAuth0Id(auth0Id)
  } yield user
}
