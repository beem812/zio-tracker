package services

import com.auth0.jwk.UrlJwkProvider
import dtos.Auth0User
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtCirce
import pdi.jwt.JwtOptions
import sttp.client3._
import zio._

trait AuthService {
  def decodeJwt(token: String): ZIO[Any, Serializable, String]
  def retrieveAuth0User(subject: String): Task[List[Auth0User]]
}

object AuthService extends Accessible[AuthService] {
  val live: URLayer[Has[HttpService], Has[AuthService]] =
    AuthServiceLive.toLayer
}

case class AuthServiceLive(http: HttpService) extends AuthService {

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

  def retrieveAuth0User(subject: String): Task[List[Auth0User]] =
    http.get[List[Auth0User]](
      uri"https://$auth0Domain/api/v2/users/$subject", // TODO: use the config service to get the auth0Domain
      Map(
        "Authorization" -> "TODO: Build a refresh service to bring in this token periodically"
      )
    )
}
