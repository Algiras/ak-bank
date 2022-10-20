package example.router

import cats.Monad
import cats.implicits._
import example.api.Bank
import example.definition.ErrorResponse
import example.definition.Request.TransferRequest
import example.definition.Response.TransferResponse
import example.domain.Money
import example.router.Operations.transferMoney
import squants.market.USD
import sttp.model.StatusCode
import sttp.tapir.codec.newtype._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

class Operations[F[_]: Monad](bank: Bank[F]) {
  val transferMoneyRoute = transferMoney.serverLogic(request =>
    for {
      res <- bank.transfer(request.from, request.to, USD(request.amount.value))
    } yield res match {
      case Right(money) => Right(TransferResponse(request.from, Money.unsafeFrom(money.value)))
      case Left(error) =>
        error match {
          case Bank.AccountDoesNotExist(accountId)        => ErrorResponse(StatusCode.NotFound, s"Account $accountId not found")
          case Bank.InsufficientFundsInAccount(accountId) => ErrorResponse(StatusCode.BadRequest, s"Insufficient funds in $accountId")
          case Bank.Unexpected(error)                     => ErrorResponse(StatusCode.InternalServerError, error.getMessage)
        }
    }
  )
}

object Operations {
  val transferMoney = Default.defaultRoute.post
    .in("transfers")
    .in(jsonBody[TransferRequest])
    .out(jsonBody[TransferResponse])
}
