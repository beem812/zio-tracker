package services

import zio._
import sttp.capabilities.zio.ZioStreams
import sttp.capabilities.WebSockets
import sttp.client3._
import zio.json._
import sttp.model.Uri

trait HttpService {
  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A]
}

object HttpService extends Accessible[HttpService] {
  val live: URLayer[Has[SttpBackend[Task, ZioStreams with WebSockets]], Has[HttpService]] = HttpServiceLive.toLayer
}

case class HttpServiceLive(sttpBackend: SttpBackend[Task, ZioStreams with WebSockets]) extends HttpService {
  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A] =
    for {
      resp <- basicRequest
               .headers(
                 headers
               )
               .get(url)
               .send(sttpBackend)
      decodedBody <- resp.body
                      .fold(
                        message => ZIO.fail(new Error(message)),
                        _.fromJson[A].fold(message => ZIO.fail(new Error(message)), ZIO.succeed[A](_))
                      )
    } yield decodedBody

}
