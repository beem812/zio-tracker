import services.UserService
import util.Db
import zhttp.service.Server
import zio._
import zio.magic._
import services.AuthService
import util.Config
import services.HttpService
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import repos.UserRepo
import util.AccessTokenCache

object HelloWorld extends App {

  def run(args: List[String]) =
    app
      .inject(
        Db.zioConn,
        Db.live,
        UserRepo.live,
        UserService.live,
        AuthService.live,
        Config.live,
        HttpService.live,
        AccessTokenCache.live,
        AsyncHttpClientZioBackend.layer()
      )
      .exitCode

  val app = for {
    _      <- Db(_.initialize)
    server <- Server.start(8090, UserController.routes)
  } yield server
}
