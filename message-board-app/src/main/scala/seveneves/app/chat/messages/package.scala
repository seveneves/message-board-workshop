package seveneves.app.chat

import cats.data.EitherT

import scala.concurrent.Future

package object messages {
  type FutureResult[T] = EitherT[Future, String, T]
}
