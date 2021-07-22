package services

import com.auth0.jwk.UrlJwkProvider
import dtos.Auth0User
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import pdi.jwt.JwtOptions
import sttp.client3._
import util.AppConfig
import zio._
import dtos.TrackerUser
import util.AccessTokenCache

trait AuthService {
  def decodeJwt(token: String): ZIO[Any, Serializable, String]
  def findOrCreateUser(auth0Id: String): Task[TrackerUser]
  def authenticateUser(req: zhttp.http.Request): ZIO[Any, Serializable, TrackerUser]
}

object AuthService extends Accessible[AuthService] {
  val live =
    AuthServiceLive.toLayer[AuthService]
}

case class AuthServiceLive(
  userService: UserService,
  http: HttpService,
  config: AppConfig,
  tokenCache: AccessTokenCache
) extends AuthService {

  def authenticateUser(req: ZRequest): ZIO[Any, Serializable, TrackerUser] =
    for {
      token       <- ZIO.fromOption(req.getBearerToken)
      auth0UserId <- decodeJwt(token)
      user        <- findOrCreateUser(auth0UserId)
    } yield user

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

  def findOrCreateUser(auth0Id: String) =
    for {
      userOption <- userService.getUserByAuth0Id(auth0Id)
      user <- userOption match {
               case Some(foundUser) => ZIO.succeed(foundUser)
               case None =>
                 for {
                   auth0User <- retrieveAuth0User(auth0Id)
                   newUser   <- userService.createUserFromAuth0User(auth0User)
                 } yield newUser
             }
    } yield user

  private def retrieveAuth0User(subject: String): Task[Auth0User] =
    for {
      token <- tokenCache.getToken
      auth0User <- http.get[Auth0User](
                    uri"https://$auth0Domain/api/v2/users/$subject",
                    Map(
                      "Authorization" -> s"Bearer $token"
                    )
                  )
    } yield auth0User

  private val auth0Domain = config.auth0Config.domain
}
