package example.definition

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.refined._
import example.domain.{AccountId, Money}

object Response {
  @derive(encoder, decoder)
  case class AccountResponse(id: AccountId)

  @derive(encoder, decoder)
  case class BalanceResponse(id: AccountId, balance: Money)

  @derive(encoder, decoder)
  case class TransferResponse(id: AccountId, balance: Money)
}
