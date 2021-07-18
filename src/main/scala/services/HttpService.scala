package services

import zio._
import sttp.client3._
import zio.json._
import sttp.model.Uri
import sttp.client3.asynchttpclient.zio.SttpClient

trait HttpService {
  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A]

  def post[A](uri: Uri, body: Map[String, String], headers: Map[String, String])(implicit A: JsonDecoder[A]): Task[A]
}

object HttpService extends Accessible[HttpService] {
  val live: URLayer[SttpClient, Has[HttpService]] = HttpServiceLive.toLayer
}

case class HttpServiceLive(sttpBackend: SttpClient.Service) extends HttpService {
  private def decode[A](resp: Response[Either[String, String]])(implicit A: JsonDecoder[A]): Task[A] =
    resp.body
      .fold(
        message => ZIO.fail(new Error(message)),
        _.fromJson[A].fold(message => ZIO.fail(new Error(message)), ZIO.succeed[A](_))
      )

  def get[A](url: Uri, headers: Map[String, String] = Map())(implicit A: JsonDecoder[A]): Task[A] =
    basicRequest
      .headers(headers)
      .get(url)
      .send(sttpBackend)
      .flatMap(decode[A])

  def post[A](uri: Uri, body: Map[String, String], headers: Map[String, String])(
    implicit A: JsonDecoder[A]
  ): Task[A] =
    basicRequest
      .body(body)
      .headers(headers)
      .post(uri)
      .send(sttpBackend)
      .flatMap(decode[A])

}
