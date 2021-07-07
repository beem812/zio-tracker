package util
import zio.config.typesafe._
import zio.config.magnolia.DeriveConfigDescriptor._
case class Auth0Config(domain: String, clientId: String, clientSecret: String)

case class AppConfig(auth0Config: Auth0Config)

object Config {
  val configDescriptor = descriptor[AppConfig]

  val live = TypesafeConfig
    .fromHoconFile(
      new java.io.File(
        Config.getClass().getClassLoader().getResource("application.conf").getPath()
      ),
      configDescriptor
    )

  val test = TypesafeConfig
    .fromHoconFile(
      new java.io.File(
        Config.getClass().getClassLoader().getResource("testApplication.conf").getPath()
      ),
      configDescriptor
    )
}
