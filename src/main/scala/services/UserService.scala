package services
import zio._
import util.Db
import dtos.TrackerUser
import io.getquill.context.ZioJdbc
import java.sql.Connection
import zio.blocking.Blocking

trait UserService {
  def sayHello(name: String): UIO[String]
  def getUsers: Task[List[TrackerUser]]
}

object UserService extends Accessible[UserService] {
  val live: URLayer[ZioJdbc.QConnection, Has[UserService]] = UserServiceLive.toLayer
  def sayHello(name: String)                               = ZIO.serviceWith[UserService](_.sayHello(name))
}

case class UserServiceLive(conn: Connection, blocking: Blocking.Service) extends UserService {
  import Db.ctx._
  private val quillEnv = Has.allOf(blocking, conn)

  override def sayHello(name: String): UIO[String] = UIO(s"sup, $name")

  override def getUsers: Task[List[TrackerUser]] =
    run(quote(query[TrackerUser])).provide(quillEnv)
}
