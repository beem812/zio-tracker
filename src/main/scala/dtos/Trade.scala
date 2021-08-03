package dtos

import zio.json._
import java.time.LocalDateTime
import java.util.UUID
import zio.test._
import zio.random.Random

case class Trade(
  id: UUID,
  userId: UUID,
  ticker: String,
  action: String,
  date: LocalDateTime,
  pricePerShare: BigDecimal,
  shares: Int,
  creditDebit: String
)

case class NewTrade(
  ticker: String,
  action: String,
  date: LocalDateTime,
  pricePerShare: BigDecimal,
  shares: Int,
  creditDebit: String
) {
  def toTrade(userId: UUID) = Trade(UUID.randomUUID(), userId, ticker, action, date, pricePerShare, shares, creditDebit)
}

object Trade {
  implicit val decoder: JsonDecoder[Trade] = DeriveJsonDecoder.gen[Trade]
  implicit val encoder: JsonEncoder[Trade] = DeriveJsonEncoder.gen[Trade]

  val genTradeWithUser = (userId: UUID) =>
    for {
      id            <- Gen.anyUUID
      ticker        <- Gen.elements("AAPL", "MSFT", "GOOG")
      action        <- Gen.elements("buyToClose", "sellToOpen")
      pricePerShare <- Gen.bigDecimal(BigDecimal(0), BigDecimal(10000.0))
      shares        <- Gen.int(0, 100000000)
      creditDebit   <- Gen.elements("Credit", "Debit")
    } yield Trade(id, userId, ticker, action, LocalDateTime.now(), pricePerShare, shares, creditDebit)

  val genTrade: Gen[Random, Trade] = for {
    userId <- Gen.anyUUID
    trade  <- genTradeWithUser(userId)
  } yield trade

}

object NewTrade {
  val genNewTrade =
    for {
      ticker        <- Gen.elements("AAPL", "MSFT", "GOOG")
      action        <- Gen.elements("buyToClose", "sellToOpen")
      pricePerShare <- Gen.bigDecimal(BigDecimal(0), BigDecimal(10000.0))
      shares        <- Gen.int(0, 100000000)
      creditDebit   <- Gen.elements("Credit", "Debit")
    } yield NewTrade(ticker, action, LocalDateTime.now(), pricePerShare, shares, creditDebit)
  implicit val decoder: JsonDecoder[NewTrade] = DeriveJsonDecoder.gen[NewTrade]
  implicit val encoder: JsonEncoder[NewTrade] = DeriveJsonEncoder.gen[NewTrade]
}
