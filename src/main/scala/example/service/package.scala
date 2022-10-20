package example

import cats.Functor
import cats.effect.std.UUIDGen
import doobie._
import example.domain.AccountId
import io.estatico.newtype.Coercible
import cats.syntax.functor._

package object service {
  implicit def coercibleMeta[R, N](implicit ev: Coercible[Meta[R], Meta[N]], R: Meta[R]): Meta[N] = ev(R)

  def genAccountId[F[_]: UUIDGen: Functor]: F[AccountId] = UUIDGen.randomUUID[F].map(AccountId(_))
}
