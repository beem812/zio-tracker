package aspects

import util.Db
import zio.test.TestAspect.before
import zio._

object DbAspects {
  import Db.ctx._
  def migrate = {
    val migration = ZIO.service[Db].flatMap(db => db.initialize)

    before(migration.orDie)
  }

  def loadTestData[A](testData: A) =
    run(quote(query[A].insert(testData)))
}
