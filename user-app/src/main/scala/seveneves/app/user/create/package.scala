package seveneves.app.user

import cats.data.EitherT

import scala.concurrent.Future

package object create {
  type FutureResult[T] = EitherT[Future, String, T]
}
