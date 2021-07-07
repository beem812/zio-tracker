import zhttp.http._
import services.UserService
import zio.json._

object UserController {
  val routes = Http.collectM[Request] {
    case Method.GET -> Root / "hello" / name =>
      for {
        greeting <- UserService.sayHello(name)
      } yield Response.text(greeting)

    case Method.GET -> Root / "users" =>
      for {
        users <- UserService(_.getUsers)
      } yield Response.jsonString(users.toJson)
  }
}
