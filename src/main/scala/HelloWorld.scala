import zio.App
import zio.console._
import zhttp.service.Server
import zio.magic._
import services.UserService

object HelloWorld extends App {

  def run(args: List[String]) = {
    appLogic2.exitCode
  }

  val routes = UserController.routes

  val myAppLogic =
    for {
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to ZIO!")
    } yield ()

  val appLogic2 = Server.start(8090, routes).inject(UserService.live)
}
