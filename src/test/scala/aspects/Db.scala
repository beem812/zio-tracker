package aspects

import util.Db
import zio.test.TestAspect.before
import zio._

object DbAspects {
  def migrate = {
    val migration = ZIO.service[Db].flatMap(db => db.initialize)

    before(migration.orDie)
  }
}
