import zhttp.http._
import services.UserService

object UserController {
  val routes = Http.collectM[Request]{
    case Method.GET -> Root / "hello" / name => for {
      greeting <- UserService.sayHello(name)
    } yield Response.text(greeting)
  }
}
