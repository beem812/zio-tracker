package dtos

import zio.json.jsonField
import zio.json._

case class Auth0User(@jsonField("created_at") createdAt: String, email: String, @jsonField("user_id") userId: String)

object Auth0User {
  implicit val decoder: JsonDecoder[Auth0User] = DeriveJsonDecoder.gen[Auth0User]
  implicit val encoder: JsonEncoder[Auth0User] = DeriveJsonEncoder.gen[Auth0User]
}
