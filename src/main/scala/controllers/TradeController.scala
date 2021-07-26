package controllers

import dtos.TrackerUser
import zhttp.http._
import services.TradeService
import zio.json._
import middleware.Authenticate
import dtos.NewTrade
import util.BodyParser

object TradeController {
  private def withUser(user: TrackerUser) = Http.collectM[Request] {
    case Method.GET -> Root / "trades" =>
      TradeService(_.getTradesByUserId(user.id)).map(trades => Response.jsonString(trades.toJson))

    case Method.GET -> Root / "trades" / ticker =>
      TradeService(_.getUserTradesByTicker(user.id, ticker)).map(trades => Response.jsonString(trades.toJson))

    case req @ Method.POST -> Root / "trades" =>
      for {
        trade    <- BodyParser.parse[NewTrade](req)
        newTrade <- TradeService(_.insertTrade(user, trade))
      } yield Response.jsonString(newTrade.toJson)
  }

  val authedRoutes = Authenticate.middleware(withUser)
}
