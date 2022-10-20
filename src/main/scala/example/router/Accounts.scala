package example.router

import cats.Functor
import example.definition.Response.{AccountResponse, BalanceResponse}
import example.router.Accounts.{accountBalance, createAccount}
import sttp.tapir.generic.auto._
import sttp.tapir.codec.newtype._
import sttp.tapir.json.circe.jsonBody
import cats.syntax.functor._
import example.definition.{ErrorResponse, Response}
import example.domain.{AccountId, Money}
import example.api.Bank
import sttp.model.StatusCode
import sttp.tapir.path

class Accounts[F[_]: Functor](bank: Bank[F]) {
  def createAccountRoute = createAccount
    .serverLogicSuccess(_ => bank.createAccount.map(Response.AccountResponse(_)))

  def getAccountBalance = accountBalance
    .serverLogic(id =>
      bank.getBalance(id).map {
        case Some(balance) => Right(Response.BalanceResponse(id, Money.unsafeFrom(balance.value)))
        case None          => ErrorResponse(StatusCode.NotFound, "Account not found")
      }
    )
}

object Accounts {
  val accountRoutes = Default.defaultRoute.in("accounts")

  val createAccount = accountRoutes.post
    .out(jsonBody[AccountResponse])
    .description("Create account")

  val accountBalance = accountRoutes.get
    .in(path[AccountId]("accountId"))
    .out(jsonBody[BalanceResponse])
    .description("Get account balance")
}
