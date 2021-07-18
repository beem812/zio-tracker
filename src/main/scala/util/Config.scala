package util
import zio.config.typesafe._
import zio.config.magnolia.DeriveConfigDescriptor._
import zio.test.TestFailure
case class Auth0Config(domain: String, clientId: String, clientSecret: String)

case class AppConfig(auth0Config: Auth0Config)

object Config {
  val configDescriptor = descriptor[AppConfig]

  val live = TypesafeConfig.fromDefaultLoader(configDescriptor)

  val test = TypesafeConfig
    .fromHoconFile(
      new java.io.File(
        Config.getClass().getClassLoader().getResource("testApplication.conf").getPath()
      ),
      configDescriptor
    )
    .mapError(TestFailure.fail)
}
