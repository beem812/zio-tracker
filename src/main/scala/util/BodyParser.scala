package util

import zhttp.http.Request
import zio.json.JsonDecoder
import zio._
import zhttp.http.HttpError
import zio.json._

object BodyParser {
  def parse[A](req: Request)(implicit A: JsonDecoder[A]): ZIO[Any, HttpError, A] =
    req.getBodyAsString match {
      case Some(body) => body.fromJson[A].fold(message => ZIO.fail(HttpError.BadRequest(message)), (ZIO.succeed(_)))
      case None       => ZIO.fail(HttpError.BadRequest("Request Contained No Body"))
    }
}
