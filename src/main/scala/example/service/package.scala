package example

import doobie._
import io.estatico.newtype.Coercible

package object service {
  implicit def coercibleMeta[R, N](implicit ev: Coercible[Meta[R], Meta[N]], R: Meta[R]): Meta[N] = ev(R)
}
