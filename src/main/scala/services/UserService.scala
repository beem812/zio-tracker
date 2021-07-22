package services
import zio._
import dtos._
import repos.UserRepo
import java.util.UUID

trait UserService {
  def getUsers: Task[List[TrackerUser]]
  def getUserById(id: UUID): Task[Option[TrackerUser]]
  def getUserByAuth0Id(auth0Id: String): Task[Option[TrackerUser]]
  def createUserFromAuth0User(auth0User: Auth0User): Task[TrackerUser]
}

object UserService extends Accessible[UserService] {
  val live = UserServiceLive.toLayer[UserService]
}

case class UserServiceLive(userRepo: UserRepo) extends UserService {

  override def getUsers: Task[List[TrackerUser]] = userRepo.getUsers

  override def getUserById(id: UUID): Task[Option[TrackerUser]] =
    userRepo.getUserById(id)

  override def getUserByAuth0Id(auth0Id: String): Task[Option[TrackerUser]] =
    userRepo.getUserByAuth0Id(auth0Id)

  override def createUserFromAuth0User(auth0User: Auth0User): Task[TrackerUser] = {
    val newTrackerUser = TrackerUser(UUID.randomUUID(), auth0User.email, auth0User.userId)
    userRepo.insertUser(newTrackerUser)
  }
}
