package repos

import zio._
import zio.blocking.Blocking
import java.sql.Connection
import java.util.UUID
import dtos.Trade
import util.Db

trait TradeRepo {
  def getTradesByUserId(userId: UUID): Task[List[Trade]]
  def getUserTradesByTicker(userId: UUID, ticker: String): Task[List[Trade]]
  def insertTrade(trade: Trade): Task[Trade]
}

object TradeRepo {
  val live: URLayer[Has[Connection] with Blocking, Has[TradeRepo]] = TradeRepoLive.toLayer[TradeRepo]
}

case class TradeRepoLive(conn: Connection, blocking: Blocking.Service) extends TradeRepo {
  import Db.ctx._
  private val quillEnv = Has.allOf(blocking, conn)

  def getTradesByUserId(userId: UUID): Task[List[Trade]] =
    run(quote {
      query[Trade].filter(_.userId.equals(lift(userId)))
    }).provide(quillEnv)

  def getUserTradesByTicker(userId: UUID, ticker: String): Task[List[Trade]] =
    run(quote {
      query[Trade].filter(_.ticker == lift(ticker)).filter(_.userId == lift(userId))
    }).provide(quillEnv)

  def insertTrade(trade: Trade): Task[Trade] =
    run(quote {
      query[Trade].insert(lift(trade))
    }).as(trade).provide(quillEnv)
}
