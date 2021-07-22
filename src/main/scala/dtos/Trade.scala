package dtos

import zio.json._
import java.time.LocalDateTime
import java.util.UUID

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
}

object NewTrade {
  implicit val decoder: JsonDecoder[NewTrade] = DeriveJsonDecoder.gen[NewTrade]
  implicit val encoder: JsonEncoder[NewTrade] = DeriveJsonEncoder.gen[NewTrade]
}
