package services
import zio._
import zio.UIO

trait UserService {
  def sayHello(name: String): UIO[String]
}

object UserService {
  val live: ULayer[Has[UserService]] = UserServiceLive.toLayer
  def sayHello(name: String): ZIO[Has[UserService], Nothing, String] = ZIO.serviceWith[UserService](_.sayHello(name))
}

case class UserServiceLive() extends UserService {
  override def sayHello(name: String): UIO[String] = UIO(s"sup, $name")
}