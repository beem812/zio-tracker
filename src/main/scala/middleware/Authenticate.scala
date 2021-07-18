package middleware

import zhttp.http._
import services.AuthService
import dtos.TrackerUser
import zio.Has

object Authenticate {
  def middleware[R, E](app: TrackerUser => HttpApp[R with Has[AuthService], E]): HttpApp[R with Has[AuthService], E] =
    Http.flatten {
      Http.fromEffectFunction[Request](req =>
        AuthService(_.authenticateUser(req)).fold(_ => HttpApp.response(Response.http(Status.UNAUTHORIZED)), app)
      )
    }
}
