package seveneves.app.user.create

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

class UserRoutes(
  userActor: ActorRef[Users.Command],
)(implicit timeout: Timeout, scheduler: Scheduler)
    extends ErrorAccumulatingCirceSupport {

  import ApiModel._
  import io.circe.generic.auto._

  val createUser: Route = {
    pathPrefix("api" / "user") {
      pathEnd {
        post {
          entity(as[CreateUserRequest]) { req =>
            extractLog { log =>
              log.info(s"Creating user $req")
              onFutureResultComplete(Users.createUser(req, userActor)) { user =>
                log.info(s"User ${user.userId} is created")
                complete(CreateUserResponse(user.userId))
              }
            }
          }
        }
      } ~ path(Segment) { userId =>
        get {
          extractLog { log =>
            log.info(s"Getting user $userId")
            onFutureResultComplete(Users.getUser(userId, userActor)) { user =>
              complete(
                GetUserResponse(
                  name = user.name,
                  email = user.email,
                ),
              )
            }
          }
        }
      }
    } ~ path("health") {
      complete(Done)
    }
  }

  private def onFutureResultComplete[T](future: => FutureResult[T])(onSuccess: T => Route): Route = {
    extractLog { log =>
      onComplete(future.value) {
        case Success(Left(error)) =>
          complete(StatusCodes.BadRequest -> Error(error))
        case Success(Right(t)) =>
          onSuccess(t)
        case Failure(exception) =>
          log.error(exception.getMessage, exception)
          complete(StatusCodes.ServerError -> Error("internal"))
      }
    }
  }
}
