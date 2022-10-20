package example

import derevo.circe.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.NonNegative
import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions
import java.util.UUID

package object domain {
  @derive(encoder, decoder)
  @newtype case class AccountId(id: UUID)

  type Money = Double Refined NonNegative
  object Money extends RefinedTypeOps[Money, Double]
}
