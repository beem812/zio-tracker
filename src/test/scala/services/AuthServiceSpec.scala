package services

import zio.magic._
import zio.test.Assertion._
import zio.test._
import dtos.Auth0User
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import util.Config
import util.AccessTokenCache

object AuthServiceSpec extends DefaultRunnableSpec {
  def spec =
    suite("AuthServiceSpec")(
      testM("decode jwt correctly returns subject") {

        for {
          // subject <- AuthService(
          //             _.decodeJwt(
          //             )
          //           )
          resp <- AuthService(_.retrieveAuth0User("google-oauth2|114205758083895850940"))
        } yield assert(resp)(
          equalTo(
            Auth0User("2021-04-23T14:47:31.182Z", "beem132@gmail.com", "")
          )
        )
      }
    ).inject(
      HttpService.live,
      Config.live.mapError(TestFailure.fail),
      AuthService.live,
      AsyncHttpClientZioBackend.layer().mapError(TestFailure.fail),
      AccessTokenCache.accessTokenCacheLive
    )
}
