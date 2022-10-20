package example.service

import cats.data.EitherT
import cats.effect.MonadCancelThrow
import cats.effect.std.UUIDGen
import doobie.{ConnectionIO, Meta, Transactor}
import doobie.implicits.toSqlInterpolator
import example.api.Bank
import example.domain
import squants.Money
import squants.market.USD
import cats.implicits._
import doobie.implicits._
import example.api.Bank.BankError
import example.service.H2Bank.{createAccountQuery, getBalanceQuery, updateBalanceQuery}

class H2Bank[F[_]: UUIDGen: MonadCancelThrow] private (transactor: Transactor[F]) extends Bank[F] {
  val defaultMoney: Money = USD(100)

  override def createAccount: F[domain.AccountId] = for {
    id <- genAccountId[F]
    _  <- createAccountQuery(id, defaultMoney).run.transact(transactor)
  } yield id

  override def getBalance(id: domain.AccountId): F[Option[Money]] =
    getBalanceQuery(id).option.transact(transactor)

  private def getExistingAccountBalance(id: domain.AccountId) =
    getBalanceQuery(id).option.map(_.toRight(Bank.AccountDoesNotExist(id).asBankError))

  private val eitherT = EitherT.liftK[ConnectionIO, BankError]

  private def ensureAccountBalanceSufficient(from: domain.AccountId, balance: Money, amountSubtracted: Money) =
    EitherT.fromEither[ConnectionIO](if (balance >= amountSubtracted) {
      Right(())
    } else Left(Bank.InsufficientFundsInAccount(from).asBankError))

  override def transfer(from: domain.AccountId, to: domain.AccountId, amount: Money): F[Either[BankError, Money]] = {
    val transaction = for {
      fromAccount <- EitherT(getExistingAccountBalance(from))
      toAccount   <- EitherT(getExistingAccountBalance(to))
      _           <- ensureAccountBalanceSufficient(from, fromAccount, amount)
      _           <- eitherT(updateBalanceQuery(from, fromAccount - amount).run)
      _           <- eitherT(updateBalanceQuery(to, toAccount + amount).run)
      newBalance  <- eitherT(getBalanceQuery(from).unique)
    } yield newBalance

    transaction.transact(transactor).value.attempt.map {
      case Right(value) => value
      case Left(error)  => Left(Bank.Unexpected(error))
    }
  }

}

object H2Bank {
  import doobie.h2.implicits._
  implicit val money: Meta[Money] = Meta[BigDecimal].timap(USD(_))(_.amount)

  def make[F[_]: MonadCancelThrow: UUIDGen](transactor: Transactor[F]): Bank[F] =
    new H2Bank[F](transactor)

  def createAccountQuery(id: domain.AccountId, amount: Money): doobie.Update0 =
    sql"INSERT INTO Accounts (id, balance) VALUES ($id, $amount)".update

  def getBalanceQuery(id: domain.AccountId): doobie.Query0[Money] =
    sql"SELECT balance FROM Accounts WHERE id = $id".query[Money]

  def updateBalanceQuery(id: domain.AccountId, amount: Money): doobie.Update0 =
    sql"UPDATE Accounts SET balance = $amount WHERE id = $id".update
}
