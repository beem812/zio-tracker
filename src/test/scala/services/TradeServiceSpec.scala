package services

import zio.test.DefaultRunnableSpec
import java.util.UUID
import zio.test.Assertion._
import zio.test._
import zio.test.TestAspect._
import zio.magic._
import repos.TradeRepo
import util.Db
import aspects.DbAspects
import dtos._

object TradeServiceSpec extends DefaultRunnableSpec {
  import Db.ctx._
  val testData = for {
    user  <- TrackerUser.genUser.runHead.get
    trade <- Trade.genTradeWithUser(user.id).runHead.get
    _     <- DbAspects.loadTestData(trade)
  } yield trade
  val loadDataAspect = before(testData)
  val tests = suite("TradeService")(
    testM("does something") {
      val result = TradeService(_.getTradesByUserId(UUID.randomUUID()))
      assertM(result)(equalTo(List()))
    }
  ) @@ DbAspects.migrate

  def spec = tests.inject(TradeService.live, TradeRepo.live, Db.zioConnTest, Db.test)
}
