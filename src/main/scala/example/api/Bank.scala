package example.api

import example.api.Bank.BankError
import example.domain.AccountId
import squants.Money

trait Bank[F[_]] {
  def createAccount: F[AccountId]
  def getBalance(id: AccountId): F[Option[Money]]
  def transfer(from: AccountId, to: AccountId, amount: Money): F[Either[BankError, Money]]
}

object Bank {
  sealed trait BankError { self =>
    val asBankError: BankError = self
  }

  case class AccountDoesNotExist(accountId: AccountId)        extends BankError
  case class InsufficientFundsInAccount(accountId: AccountId) extends BankError
  case class Unexpected(error: Throwable)                     extends BankError
}
