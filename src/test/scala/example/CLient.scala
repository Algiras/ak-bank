package example

import cats.MonadThrow
import example.Client.ClientError.{DecodeError, ResponseError}
import example.definition.ErrorResponse
import example.domain.{AccountId, Money}
import example.router.{Accounts, Operations}
import sttp.client3.SttpBackend
import sttp.model.Uri
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.{DecodeResult, PublicEndpoint}
import cats.implicits._
import example.definition.Request.TransferRequest

import scala.util.control.NoStackTrace

trait Client[F[_]] {
  def createAccount: F[AccountId]
  def getBalance(id: AccountId): F[Money]
  def transfer(from: AccountId, to: AccountId, amount: Money): F[Money]
}

object Client {
  sealed trait ClientError extends NoStackTrace

  object ClientError {
    case object DecodeError                                extends ClientError
    case class ResponseError(status: Int, message: String) extends ClientError
  }

  def make[F[_], R](baseUri: Uri, backend: SttpBackend[F, R])(implicit F: MonadThrow[F]): Client[F] = new Client[F] {
    private val interpreter = SttpClientInterpreter()
    private def partialRequest[I, E, O](request: PublicEndpoint[I, E, O, R]): I => F[DecodeResult[Either[E, O]]] =
      interpreter.toClient(request, Some(baseUri), backend)

    private def handleResponse[T](from: DecodeResult[Either[ErrorResponse, T]]): F[T] =
      from match {
        case _: DecodeResult.Failure => F.raiseError(DecodeError)
        case DecodeResult.Value(result) =>
          result match {
            case Left(error)  => F.raiseError(ResponseError(error.statusCode, error.message))
            case Right(value) => F.pure(value)
          }
      }

    override def createAccount: F[AccountId] = partialRequest(Accounts.createAccount)(())
      .flatMap(handleResponse(_))
      .map(_.id)

    override def getBalance(accountId: AccountId): F[Money] = partialRequest(Accounts.accountBalance)(accountId)
      .flatMap(handleResponse(_))
      .map(_.balance)

    override def transfer(from: AccountId, to: AccountId, amount: Money): F[Money] = partialRequest(Operations.transferMoney)(
      TransferRequest(from, to, amount)
    ).flatMap(handleResponse(_)).map(_.balance)
  }
}
