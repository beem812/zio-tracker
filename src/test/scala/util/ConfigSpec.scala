package util

import zio.test._
import zio.test.Assertion._
import zio.magic._
import zio.ZIO

object ConfigSPec extends DefaultRunnableSpec {
  def spec =
    suite("ConfigSpec")(
      testM("correctly loads the app config") {
        for {

          domain <- ZIO.serviceWith[AppConfig]((config) => ZIO.succeed(config.auth0Config.domain))
        } yield assert(domain)(equalTo("test-domain"))

      }
    ).inject(Config.test.mapError((e) => TestFailure.fail(e)))
}
