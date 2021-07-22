package services
import zio._
import java.util.UUID
import repos.TradeRepo
import dtos._

trait TradeService {
  def getTradesByUserId(userId: UUID): Task[List[Trade]]
  def getUserTradesByTicker(userId: UUID, ticker: String): Task[List[Trade]]
  def insertTrade(user: TrackerUser, trade: NewTrade): Task[Trade]
}

object TradeService extends Accessible[TradeService] {
  val live = TradeServiceLive.toLayer[TradeService]
}

case class TradeServiceLive(tradeRepo: TradeRepo) extends TradeService {

  override def insertTrade(user: TrackerUser, newTrade: NewTrade): Task[Trade] = {
    val trade: Trade = newTrade.toTrade(user.id)

    tradeRepo.insertTrade(trade)
  }

  override def getUserTradesByTicker(userId: UUID, ticker: String): Task[List[Trade]] =
    tradeRepo.getUserTradesByTicker(userId, ticker)

  override def getTradesByUserId(userId: UUID): Task[List[Trade]] = tradeRepo.getTradesByUserId(userId)

}
