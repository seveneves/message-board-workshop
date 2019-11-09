package seveneves.app.user.create

import java.util.UUID

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import cats.data.EitherT
import seveneves.app.user.create.proto.UserCreated.UserCreated

object Users {

  final case class User(
    userId: String,
    name: String,
    email: String,
  )

  sealed trait Command

  final case class StoreUser(
    name: String,
    email: String,
    replyTo: ActorRef[Either[String, User]],
  ) extends Command

  final case class GetUser(
    userId: String,
    replyTo: ActorRef[Either[String, User]],
  ) extends Command

  def storeUsersBehaviour(users: Map[String, User])(implicit kafkaExt: KafkaProducer): Behavior[Command] = Behaviors.receiveMessage {
    case storeUser: StoreUser if users.values.exists(_.email == storeUser.email) =>
      storeUser.replyTo.tell(Left("User exists"))
      Behaviors.same
    case storeUser: StoreUser =>
      val user = User(userId = UUID.randomUUID().toString, storeUser.name, storeUser.email)
      kafkaExt.pushToKafka(UserCreated(user.name, user.email, user.userId))
      storeUser.replyTo.tell(Right(user))
      storeUsersBehaviour(users + (user.userId -> user))
    case getUser: GetUser if users.contains(getUser.userId) =>
      getUser.replyTo.tell(Right(users(getUser.userId)))
      Behaviors.same
    case getUser: GetUser =>
      getUser.replyTo.tell(Left("Not Found"))
      Behaviors.same
  }

  def createUser(
    createUserRequest: ApiModel.CreateUserRequest,
    actorRef: ActorRef[Command],
  )(implicit timeout: Timeout, scheduler: Scheduler): FutureResult[User] = {
    EitherT(actorRef ? (StoreUser(createUserRequest.name, createUserRequest.email, _)))
  }

  def getUser(
    userId: String,
    actorRef: ActorRef[Command],
  )(implicit timeout: Timeout, scheduler: Scheduler): FutureResult[User] = {
    EitherT(actorRef ? (GetUser(userId, _)))
  }

}
