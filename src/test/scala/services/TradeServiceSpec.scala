package services

import zio.test.Assertion._
import zio.test._
import zio.test.TestAspect._
import zio.magic._
import repos.TradeRepo
import util.Db
import aspects.DbAspects
import dtos._
import zio.random.Random
import testTools.TestRepo
import java.util.UUID

object TradeServiceSpec extends DefaultRunnableSpec {
  val userId = UUID.randomUUID()

  val testData = for {
    trade <- Trade.genTradeWithUser(userId).runHead.someOrFailException
    _     <- TestRepo(_.insert(trade))
  } yield trade

  val loadDataAspect = before(testData.orDie)
  val tests = suite("TradeService")(
    suite("getTradesByUserId()") {
      testM("gets trades for a given user") {
        for {
          trade  <- testData
          result <- TradeService(_.getTradesByUserId(userId))
        } yield assert(result)(equalTo(List(trade)))
      }
    },
    suite("getUserTradesByTicker()") {
      testM("takes a TrackerUser and a ticker, and returns all that users trades of a given ticker") {
        for {
          user            <- TrackerUser.genUser.runHead.someOrFailException
          appleTrades     <- Trade.genTradeWithUser(user.id).map(_.copy(ticker = "AAPL")).runCollectN(3)
          nvidiaTrades    <- Trade.genTradeWithUser(user.id).map(_.copy(ticker = "NVDA")).runCollectN(3)
          _               <- TestRepo(_.insert(appleTrades ++ nvidiaTrades))
          retrievedTrades <- TradeService(_.getUserTradesByTicker(user.id, "AAPL"))
        } yield assert(retrievedTrades)(equalTo(appleTrades))
      }
    },
    suite("insertTrade()") {
      testM("takes a TrackerUser and a NewTrade, and inserts the trade") {
        for {
          user            <- TrackerUser.genUser.runHead.someOrFailException
          newTrade        <- NewTrade.genNewTrade.runHead.someOrFailException
          trade           <- TradeService(_.insertTrade(user, newTrade))
          retrievedTrades <- TradeService(_.getTradesByUserId(user.id))
        } yield assert(retrievedTrades)(equalTo(List(trade)))
      }
    }
  ) @@ DbAspects.migrate

  def spec =
    tests.inject(
      TradeService.live,
      TradeRepo.live,
      Db.zioConnTest,
      Db.test,
      Random.live,
      TestRepo.live,
      Sized.live(100)
    )
}
