package example.definition

import derevo.circe.{decoder, encoder}
import derevo.derive
import sttp.model.StatusCode

@derive(encoder, decoder)
case class ErrorResponse(statusCode: Int, message: String)

object ErrorResponse {
  type Response[T] = Either[ErrorResponse, T]

  def apply[T](status: StatusCode, message: String): Response[T] = Left(new ErrorResponse(status.code, message))
}
