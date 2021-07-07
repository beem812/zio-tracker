package dtos

import zio.json.jsonField
import zio.json._

case class AccessTokenResponse(@jsonField("access_token") accessToken: String)

object AccessTokenResponse {
  implicit val decoder: JsonDecoder[AccessTokenResponse] = DeriveJsonDecoder.gen[AccessTokenResponse]
  implicit val encoder: JsonEncoder[AccessTokenResponse] = DeriveJsonEncoder.gen[AccessTokenResponse]
}
