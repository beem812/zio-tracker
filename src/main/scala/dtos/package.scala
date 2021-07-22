import zio.json._
import java.time.LocalDateTime
import io.getquill._
import java.util.UUID

package object dtos {

  implicit val dateDecoder: JsonDecoder[LocalDateTime] =
    JsonDecoder[String].map((strDate: String) => LocalDateTime.parse(strDate))
  implicit val dateEncoder: JsonEncoder[LocalDateTime] =
    JsonEncoder[String].contramap(_.toString())

  implicit val encodeUUID = MappedEncoding[UUID, String](_.toString)
  implicit val decodeUUID = MappedEncoding[String, UUID](UUID.fromString(_))
}
