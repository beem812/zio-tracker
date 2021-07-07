package services

import zio._
import sttp.capabilities.zio.ZioStreams
import sttp.capabilities.WebSockets
import sttp.client3._
import zio.json._
import sttp.model.Uri

trait HttpService {
  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A]

  def post[A](uri: Uri, body: Map[String, String], headers: Map[String, String])(implicit A: JsonDecoder[A]): Task[A]
}

object HttpService extends Accessible[HttpService] {
  val live: URLayer[Has[SttpBackend[Task, ZioStreams with WebSockets]], Has[HttpService]] = HttpServiceLive.toLayer
}

case class HttpServiceLive(sttpBackend: SttpBackend[Task, ZioStreams with WebSockets]) extends HttpService {
  private def decode[A](body: Either[String, String])(implicit A: JsonDecoder[A]): Task[A] =
    body
      .fold(
        message => ZIO.fail(new Error(message)),
        _.fromJson[A].fold(message => ZIO.fail(new Error(message)), ZIO.succeed[A](_))
      )

  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A] =
    for {
      resp <- basicRequest
               .headers(
                 headers
               )
               .get(url)
               .send(sttpBackend)

      decodedBody <- decode[A](resp.body)
    } yield decodedBody

  def post[A](uri: Uri, body: Map[String, String], headers: Map[String, String])(
    implicit A: JsonDecoder[A]
  ): Task[A] =
    for {
      resp        <- basicRequest.body(body).headers(headers).post(uri).send(sttpBackend)
      decodedBody <- decode[A](resp.body)
    } yield decodedBody

}
