package services

import zio.test._
import zio.test.Assertion._
import zio.magic._
import util.Db

object UserServiceSpec extends DefaultRunnableSpec {
  def spec =
    suite("UserServiceSpec")(
      testM("sayHello correctly displays output") {
        for {
          greeting <- UserService.sayHello("dab")
        } yield assert(greeting)(equalTo("sup, dab"))
      }
    ).inject(UserService.live, Db.zioConn.mapError(TestFailure.fail))
}
