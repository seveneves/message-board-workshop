package seveneves.app.chat.messages

import akka.Done
import akka.actor.typed.ActorRef
import akka.actor.typed.Scheduler
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport

import scala.util.Failure
import scala.util.Success

class MessageRoutes(
  implicit messageProcessor: ActorRef[Messages.Command],
  timeout: Timeout,
  scheduler: Scheduler,
) extends ErrorAccumulatingCirceSupport {

  import ApiModel._

  val api: Route = {
    pathPrefix("api" / "chat") {
      import io.circe.generic.auto._
      pathEnd {
        put {
          entity(as[SendMessage]) { req =>
            extractLog { log =>
              log.info(s"Sending message to ${req.userId}")
              onFutureResultComplete(Messages.send(req.userId, req.message)) { _ =>
                log.info(s"Message sent to ${req.userId}")
                complete(Done)
              }
            }
          }
        }
      } ~ path(Segment) { userId =>
        get {
          extractLog { log =>
            log.info(s"Getting messages for user $userId")
            onFutureResultComplete(Messages.retrieve(userId)) { messages =>
              complete(MessagesResponse(messages))
            }
          }
        }
      }
    } ~ path("health") {
      complete(Done)
    }
  }

  private def onFutureResultComplete[T](future: => FutureResult[T])(onSuccess: T => Route): Route = {
    import io.circe.generic.auto._
    extractLog { log =>
      onComplete(future.value) {
        case Success(Left(error)) =>
          complete(StatusCodes.BadRequest -> Error(error))
        case Success(Right(t)) =>
          onSuccess(t)
        case Failure(exception) =>
          log.error(exception.getMessage, exception)
          complete(StatusCodes.InternalServerError -> Error("internal"))
      }
    }
  }
}
