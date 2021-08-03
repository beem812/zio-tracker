package testTools

import zio.blocking.Blocking
import java.sql.Connection
import util.Db
import zio._
import dtos.Trade

trait TestRepo {
  def insert(data: Trade): Task[Trade]
  def insert(data: List[Trade]): Task[List[Trade]]
}

object TestRepo extends Accessible[TestRepo] {
  val live = TestRepoLive.toLayer[TestRepo]
}

final case class TestRepoLive(conn: Connection, blocking: Blocking.Service) extends TestRepo {
  import Db.ctx._

  private val quillEnv = Has.allOf(blocking, conn)

  def insert(data: Trade): Task[Trade] =
    run(quote(query[Trade].insert(lift(data)))).as(data).provide(quillEnv)

  def insert(data: List[Trade]): Task[List[Trade]] =
    run(quote(liftQuery(data).foreach(t => query[Trade].insert(t)))).as(data).provide(quillEnv)
}
