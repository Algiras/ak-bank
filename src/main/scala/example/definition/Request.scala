package example.definition

import derevo.circe.{decoder, encoder}
import derevo.derive
import example.domain.{AccountId, Money}
import io.circe.refined._

object Request {
  @derive(encoder, decoder)
  case class TransferRequest(from: AccountId, to: AccountId, amount: Money)
}
