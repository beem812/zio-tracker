package services

import zio.magic._
import zio.test.Assertion._
import zio.test._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import util.Config
import util.AccessTokenCache
import util.Db
import repos.UserRepo
import dtos.Auth0User
import zio._
import zio.json._
import sttp.client3.asynchttpclient.zio.SttpClient
import java.time.LocalDateTime

object AuthServiceSpec extends DefaultRunnableSpec {
  val stubSttp: ULayer[SttpClient] =
    ZLayer.succeed(
      AsyncHttpClientZioBackend.stub.whenAnyRequest
        .thenRespond(Auth0User(LocalDateTime.now().toString(), "testuser@test", "google-oauth2|fake").toJson)
    )

  def spec =
    suite("AuthServiceSpec")(
      testM("decode jwt correctly returns subject") {

        val testId = "google-oauth2|fake"
        for {
          _    <- Db(_.initialize)
          resp <- AuthService(_.findOrCreateUser(testId))
        } yield assert(resp)(
          hasField("auth0Id", _.auth0Id, equalTo(testId))
        )
      }
    ).inject(
      Db.test,
      Db.zioConnTest,
      UserRepo.live,
      UserService.live,
      HttpService.live,
      Config.live.orDie,
      stubSttp,
      AuthService.live,
      AccessTokenCache.test
    )
}
