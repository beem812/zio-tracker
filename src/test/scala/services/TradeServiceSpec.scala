package services

import zio.test.DefaultRunnableSpec
import java.util.UUID
import zio.test.Assertion._
import zio.test._
import zio.magic._
import repos.TradeRepo
import util.Db
import aspects.DbAspects

object TradeServiceSpec extends DefaultRunnableSpec {
  val tests = suite("TradeService")(
    testM("does something") {
      val result = TradeService(_.getTradesByUserId(UUID.randomUUID()))
      assertM(result)(equalTo(List()))
    }
  ) @@ DbAspects.migrate

  def spec = tests.inject(TradeService.live, TradeRepo.live, Db.zioConnTest, Db.test)
}
