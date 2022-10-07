package example

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.specs2.mutable.Specification
import doobie.specs2.IOChecker
import doobie.util.transactor.Transactor
import example.database.DbSessionPool
import example.database.Migration

class QueriesIT extends Specification with IOChecker {
  Migration[IO](databaseConfig).unsafeRunSync()

  override def transactor = Transactor.fromDriverManager(
    DbSessionPool.driverClassName,
    databaseConfig.url.value,
    databaseConfig.user.value,
    databaseConfig.password.value
  )
}
