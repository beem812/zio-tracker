package util

import zio._
import services.HttpService
import dtos.AccessTokenResponse
import zio.cache.Lookup
import zio.cache.Cache
import zio.duration.Duration
import java.util.concurrent.TimeUnit
import sttp.client3._

object AccessTokenCache {
  val accessTokenCacheLive = ZLayer.fromEffect {

    val thing = for {

      config <- ZIO.service[AppConfig]
      http   <- ZIO.service[HttpService]
      fetchAccessToken = (body: Map[String, String]) => {

        val domain = config.auth0Config.domain

        for {
          accessTokenResponse <- http.post[AccessTokenResponse](
                                  uri"https://$domain/oauth/token",
                                  body,
                                  Map("content-type" -> "application/x-www-form-urlencoded")
                                )
        } yield accessTokenResponse.accessToken
      }
      cache <- Cache.make(1, Duration(4, TimeUnit.HOURS), Lookup((body: Map[String, String]) => fetchAccessToken(body)))
    } yield cache
    thing
  }
}
