package example

import cats.effect.{IO, Resource}
import cats.effect.testing.specs2.CatsResource
import example.Client.ClientError.ResponseError
import org.specs2.mutable.SpecificationLike
import eu.timepit.refined.auto._

class BankE2E extends CatsResource[IO, Client[IO]] with SpecificationLike {
  override val resource: Resource[IO, Client[IO]] = clientResource

  "Bank" should {
    "create accounts with unique ids" in withResource { client =>
      for {
        id1 <- client.createAccount
        id2 <- client.createAccount
      } yield id1 must_!== id2
    }

    "retrieve account balance" in withResource { client =>
      for {
        account <- client.createAccount
        balance <- client.getBalance(account)
      } yield balance.value must_=== 100
    }

    "getting balance for non-existing account should fail with 404" in withResource { client =>
      for {
        fakeAccount <- service.genAccountId[IO]
        error       <- client.getBalance(fakeAccount).attempt
      } yield error must beLeft(ResponseError(404, "Account not found"))
    }

    "account can transfer money to another account" in withResource { client =>
      for {
        acc1           <- client.createAccount
        acc2           <- client.createAccount
        acc1Balance    <- client.getBalance(acc1)
        acc2Balance    <- client.getBalance(acc2)
        acc1NewBalance <- client.transfer(acc1, acc2, 1.0)
        acc2NewBalance <- client.getBalance(acc2)
      } yield {
        acc1NewBalance.value must_=== (acc1Balance.value - 1.0)
        acc2NewBalance.value must_=== (acc2Balance.value + 1.0)
      }
    }
  }
}
