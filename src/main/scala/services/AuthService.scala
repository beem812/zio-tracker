package services

import com.auth0.jwk.UrlJwkProvider
import dtos.Auth0User
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import pdi.jwt.JwtOptions
import sttp.client3._
import util.AppConfig
import zio._
import zio.cache.Cache

trait AuthService {
  def decodeJwt(token: String): ZIO[Any, Serializable, String]
  def retrieveAuth0User(subject: String): Task[Auth0User]
}

object AuthService extends Accessible[AuthService] {
  val live: URLayer[Has[HttpService] with Has[AppConfig] with Has[Cache[Map[String, String], Throwable, String]], Has[
    AuthService
  ]] =
    AuthServiceLive.toLayer
}

case class AuthServiceLive(
  http: HttpService,
  config: AppConfig,
  tokenCache: Cache[Map[String, String], Throwable, String]
) extends AuthService {

  val auth0Domain = config.auth0Config.domain
  val secret      = config.auth0Config.clientSecret
  val clientId    = config.auth0Config.clientId
  val accessTokenRequestBody = Map[String, String](
    "grant_type"    -> "client_credentials",
    "client_id"     -> clientId,
    "client_secret" -> secret,
    "audience"      -> uri"https://$auth0Domain/api/v2/".toString()
  )

  def decodeJwt(token: String): ZIO[Any, Serializable, String] = {
    val tokenParts  = ZIO.fromTry(JwtCirce.decodeAll(token, JwtOptions(signature = false)))
    val jwkProvider = new UrlJwkProvider(auth0Domain)

    for {
      (header, _, _) <- tokenParts
      jwk            = jwkProvider.get(header.keyId.getOrElse(""))
      claims         <- ZIO.fromTry(JwtCirce.decode(token, jwk.getPublicKey(), Seq(JwtAlgorithm.RS256)))
      subject        <- ZIO.fromOption(claims.subject)
    } yield subject
  }

  def retrieveAuth0User(subject: String): Task[Auth0User] =
    for {
      token <- tokenCache.get(accessTokenRequestBody)
      auth0User <- http.get[Auth0User](
                    uri"https://$auth0Domain/api/v2/users/$subject",
                    Map(
                      "Authorization" -> s"Bearer $token"
                    )
                  )
    } yield auth0User
}
