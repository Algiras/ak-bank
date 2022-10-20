package example

import cats.effect.{IO, Resource}
import cats.effect.testing.specs2.CatsResource
import example.api.Bank
import org.specs2.mutable.SpecificationLike
import squants.market.USD

class BankIT extends CatsResource[IO, Bank[IO]] with SpecificationLike {
  override val resource: Resource[IO, Bank[IO]] = bankResource
  private val dollar                            = USD(1)

  "Bank" should {
    "fail transfer if" >> {
      "from account does not exists" in withResource { bank =>
        for {
          acc1        <- bank.createAccount
          fakeAccount <- service.genAccountId[IO]
          response    <- bank.transfer(fakeAccount, acc1, dollar)
        } yield response must beLeft(Bank.AccountDoesNotExist(fakeAccount))
      }

      "to account does not exists" in withResource { bank =>
        for {
          acc1        <- bank.createAccount
          fakeAccount <- service.genAccountId[IO]
          response    <- bank.transfer(acc1, fakeAccount, dollar)
        } yield response must beLeft(Bank.AccountDoesNotExist(fakeAccount))
      }

      "from account does not have enough money" in withResource { bank =>
        for {
          from        <- bank.createAccount
          fromBalance <- bank.getBalance(from).flatMap(IO.fromOption(_)(new RuntimeException("Account does not exist")))
          to          <- bank.createAccount
          response    <- bank.transfer(from, to, fromBalance + dollar)
        } yield response must beLeft(Bank.InsufficientFundsInAccount(from))
      }
    }

  }
}
