package example

import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.effect.unsafe.implicits.global
import org.specs2.mutable.Specification
import doobie.specs2.IOChecker
import doobie.util.transactor.Transactor
import example.database.DbSessionPool
import example.database.Migration
import example.domain.AccountId
import example.service.H2Bank
import squants.market.USD

class QueriesIT extends Specification with IOChecker {
  Migration[IO](databaseConfig).unsafeRunSync()
  private val accountId = UUIDGen.randomUUID[IO].map(AccountId(_)).unsafeRunSync()
  private val dollar    = USD(1)

  override def transactor = Transactor.fromDriverManager(
    DbSessionPool.driverClassName,
    databaseConfig.url.value,
    databaseConfig.user.value,
    databaseConfig.password.value
  )

  check(H2Bank.createAccountQuery(accountId, dollar))
  check(H2Bank.getBalanceQuery(accountId))
  check(H2Bank.updateBalanceQuery(accountId, dollar))
}
