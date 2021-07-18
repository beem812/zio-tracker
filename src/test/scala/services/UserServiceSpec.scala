package services

import zio.test._
import zio.test.Assertion._
import zio.magic._
import zio._
import util.Db
import dtos.Auth0User
import repos.UserRepo
import dtos.TrackerUser
import aspects.DbAspects

object UserServiceSpec extends DefaultRunnableSpec {
  def userSeriveSpec =
    suite("UserServiceSpec")(
      testM("createUserFromAuth0User generates a new user from the provided auth0 user data") {
        val auth0User = Auth0User("a date", "an email", "my auth0 user id")
        val newUser   = UserService(_.createUserFromAuth0User(auth0User))
        assertM(newUser)(
          hasField("email", (u: TrackerUser) => u.email, equalTo(auth0User.email)) &&
            hasField("auth0Id", (u: TrackerUser) => u.auth0Id, equalTo(auth0User.userId))
        )
      },
      testM("getUserByAuth0Id gets the correct user") {
        val auth0User = Auth0User("a date", "an email", "my auth0 user id")
        for {
          newUser         <- UserService(_.createUserFromAuth0User(auth0User))
          foundUserOption <- UserService(_.getUserByAuth0Id(auth0User.userId))
          foundUser       <- ZIO.fromOption(foundUserOption)
        } yield assert(foundUser)(equalTo(newUser))
      }
    ) @@ DbAspects.migrate

  def spec =
    userSeriveSpec.inject(
      UserService.live,
      UserRepo.live,
      Db.zioConnTest,
      Db.test
    )
}
