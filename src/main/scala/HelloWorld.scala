import services.UserService
import util.Db
import zhttp.service.Server
import zio.App
import zio.magic._

object HelloWorld extends App {

  def run(args: List[String]) =
    app.inject(Db.zioConn, Db.dataSourceLayer, UserService.live, Db.live).exitCode

  val routes = UserController.routes

  val app = for {
    _      <- Db(_.initialize)
    server <- Server.start(8090, routes)
  } yield server
}
