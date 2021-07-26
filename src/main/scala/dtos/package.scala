import zio.json._
import java.time.LocalDateTime
import io.getquill._
import java.util.UUID
import zhttp.http.Response

trait Dto {
  def toZhttpJsonResponse(implicit encoder: JsonEncoder[Dto]) = Response.jsonString(this.toJson)

}
package object dtos {
  implicit val dateDecoder: JsonDecoder[LocalDateTime] =
    JsonDecoder[String].map((strDate: String) => LocalDateTime.parse(strDate))
  implicit val dateEncoder: JsonEncoder[LocalDateTime] =
    JsonEncoder[String].contramap(_.toString())

  implicit val encodeUUID = MappedEncoding[UUID, String](_.toString)
  implicit val decodeUUID = MappedEncoding[String, UUID](UUID.fromString(_))
}
