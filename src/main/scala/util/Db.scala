package util

import io.getquill._
import io.getquill.context.ZioJdbc
import zio.blocking.effectBlocking
import org.flywaydb.core.Flyway
import zio.blocking.Blocking
import zio._
import org.flywaydb.core.api.output.MigrateResult
import io.getquill.util.LoadConfig
import com.zaxxer.hikari.HikariDataSource
import zio.test.TestFailure

trait Db {
  def initialize: IO[Throwable, MigrateResult]
}

object Db extends Accessible[Db] {
  val ctx = new H2ZioJdbcContext(SnakeCase)

  def config = JdbcContextConfig(LoadConfig("h2DbConfig")).dataSource

  def dataSourceLayer = Task(config).toManaged_.toLayer

  def zioConn =
    Blocking.live >>> ZioJdbc.QDataSource.fromPrefix("h2DbConfig") >>> ZioJdbc.QDataSource.toConnection

  def zioConnTest =
    (Blocking.live >>> ZioJdbc.QDataSource.fromPrefix("h2DbConfig") >>> ZioJdbc.QDataSource.toConnection)
      .mapError(TestFailure.fail)

  def live: ZLayer[Any, Throwable, Has[Db]] = dataSourceLayer >>> DbLive.toLayer[Db]

  def test: ZLayer[Any, TestFailure[Throwable], Has[Db]] =
    (dataSourceLayer >>> DbLive.toLayer[Db]).mapError(TestFailure.fail)
}

case class DbLive(dataSource: HikariDataSource) extends Db {

  def initialize =
    effectBlocking {
      Flyway.configure().dataSource(dataSource).load().migrate()
    }.provideLayer(Blocking.live)
}
