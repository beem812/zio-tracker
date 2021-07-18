package util

import zio._
import services.HttpService
import dtos.AccessTokenResponse
import zio.cache.Lookup
import zio.cache.Cache
import zio.duration.Duration
import java.util.concurrent.TimeUnit
import sttp.client3._

trait AccessTokenCache {
  def getToken: Task[String]
}

object AccessTokenCache {
  type TokenCache = Cache[Map[String, String], Throwable, String]

  val liveFetchBuilder = (domain: String) =>
    for {
      http <- ZIO.service[HttpService]
      fetchToken = (body: Map[String, String]) => {
        http
          .post[AccessTokenResponse](
            uri"https://$domain/oauth/token",
            body,
            Map("content-type" -> "application/x-www-form-urlencoded")
          )
          .map(_.accessToken)
      }
    } yield fetchToken

  val live: ZLayer[Has[AppConfig] with Has[HttpService], NoSuchElementException, Has[AccessTokenCache]] =
    ZLayer.fromEffect {
      for {
        config           <- ZIO.service[AppConfig]
        fetchAccessToken <- liveFetchBuilder(config.auth0Config.domain)
        cache <- Cache.make(
                  1,
                  Duration(4, TimeUnit.HOURS),
                  Lookup((body: Map[String, String]) => fetchAccessToken(body))
                )
      } yield AccessTokenCacheLive(config, cache)
    }

  val test: ULayer[Has[AccessTokenCache]] = AccessTokenCacheTest.toLayer[AccessTokenCache]
}

case class AccessTokenCacheLive(config: AppConfig, cache: AccessTokenCache.TokenCache) extends AccessTokenCache {
  private val auth0Domain = config.auth0Config.domain

  private val accessTokenRequestBody = Map[String, String](
    "grant_type"    -> "client_credentials",
    "client_id"     -> config.auth0Config.clientId,
    "client_secret" -> config.auth0Config.clientSecret,
    "audience"      -> uri"https://$auth0Domain/api/v2/".toString()
  )
  override def getToken = cache.get(accessTokenRequestBody)
}

case class AccessTokenCacheTest() extends AccessTokenCache {
  override def getToken: Task[String] = ZIO.succeed("test token")
}
