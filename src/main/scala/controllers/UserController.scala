import zhttp.http._
import services.UserService
import zio.json._
import dtos.TrackerUser
import middleware.Authenticate

object UserController {
  val openRoutes = Http.collectM[Request] {

    case Method.GET -> Root / "users" =>
      for {
        users <- UserService(_.getUsers)
      } yield Response.jsonString(users.toJson)
  }

  private def user(user: TrackerUser) =
    HttpApp.collectM {
      case Method.GET -> Root / "authedUsers" =>
        for {
          users <- UserService(_.getUsers)
        } yield Response.jsonString((user :: users).toJson)
    }
  val authedRoutes = Authenticate.middleware(user)

  val routes = openRoutes +++ authedRoutes
}
