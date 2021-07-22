package repos

import util.Db
import dtos.TrackerUser
import java.sql.Connection
import zio.blocking.Blocking
import zio._
import java.util.UUID

trait UserRepo {
  def getUsers: Task[List[TrackerUser]]
  def getUserById(id: UUID): Task[Option[TrackerUser]]
  def getUserByAuth0Id(auth0Id: String): Task[Option[TrackerUser]]
  def insertUser(user: TrackerUser): Task[TrackerUser]
}

object UserRepo {
  val live: URLayer[Has[Connection] with Blocking, Has[UserRepo]] = UserRepoLive.toLayer[UserRepo]
}

case class UserRepoLive(conn: Connection, blocking: Blocking.Service) extends UserRepo {
  import Db.ctx._
  private val quillEnv = Has.allOf(blocking, conn)

  def getUsers: Task[List[TrackerUser]] =
    run(quote {
      query[TrackerUser]
    }).provide(quillEnv)

  def getUserById(id: UUID): Task[Option[TrackerUser]] =
    run(quote(query[TrackerUser].filter(user => user.id == lift(id)).take(1)))
      .map(users => users.headOption)
      .provide(quillEnv)

  def getUserByAuth0Id(auth0Id: String): Task[Option[TrackerUser]] =
    run(quote(query[TrackerUser].filter(user => user.auth0Id == lift(auth0Id)).take(1)))
      .map(users => users.headOption)
      .provide(quillEnv)

  def insertUser(user: TrackerUser): Task[TrackerUser] =
    run(quote(query[TrackerUser].insert(lift(user)).returningGenerated(_.id)))
      .map(id => TrackerUser(id, user.email, user.auth0Id))
      .provide(quillEnv)

}
