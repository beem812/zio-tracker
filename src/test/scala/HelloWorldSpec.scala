import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import HelloWorld._

object HelloWorld {
  def sayHello: ZIO[Console, Nothing, Unit] =
    console.putStrLn("Hello, World!").orDie
}

object HelloWorldSpec extends DefaultRunnableSpec {
  def spec = suite("HelloWorldSpec")(
    testM("sayHello correctly displays output") {
      for {
        _      <- sayHello
        output <- TestConsole.output
      } yield assert(output)(equalTo(Vector("Hello, World!\n")))
    }
  )
}
