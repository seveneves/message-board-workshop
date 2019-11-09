package seveneves.app.chat.messages

import akka.Done
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.Scheduler
import akka.util.Timeout
import cats.data.EitherT

import scala.collection.mutable

object Messages {

  sealed trait Command

  final case class SendMessage(
    userId: String,
    message: String,
    replyTo: ActorRef[Either[String, Done]],
  ) extends Command

  final case class GetMessages(
    userId: String,
    replyTo: ActorRef[Either[String, Seq[String]]],
  ) extends Command

  final case class InitUser(userId: String) extends Command

  def messageProcessor(): Behavior[Command] = Behaviors.setup { _ =>
    val messages = mutable.Map[String, mutable.ArrayBuffer[String]]()
    Behaviors.receiveMessage {
      case cmd: SendMessage if messages.contains(cmd.userId) =>
        cmd.replyTo.tell(Right(Done))
        messages.put(cmd.userId, messages(cmd.userId).append(cmd.message))
        Behaviors.same
      case cmd: SendMessage =>
        cmd.replyTo.tell(Left("Not found"))
        Behaviors.same
      case InitUser(userId) if !messages.contains(userId) =>
        messages.put(userId, mutable.ArrayBuffer.empty)
        Behaviors.same
      case InitUser(_) =>
        Behaviors.same
      case cmd: GetMessages if messages.contains(cmd.userId) =>
        cmd.replyTo.tell(Right(messages(cmd.userId).toSeq))
        Behaviors.same
      case cmd: GetMessages =>
        cmd.replyTo.tell(Left("Not Found"))
        Behaviors.same
    }
  }

  def initUser(
    userId: String,
  )(implicit actorRef: ActorRef[InitUser]): Unit = {
    actorRef.tell(InitUser(userId))
  }

  def retrieve(
    userId: String,
  )(implicit actorRef: ActorRef[GetMessages], timeout: Timeout, scheduler: Scheduler): FutureResult[Seq[String]] = {
    EitherT(actorRef ? (GetMessages(userId, _)))
  }

  def send(
    userId: String,
    message: String,
  )(implicit actorRef: ActorRef[SendMessage], timeout: Timeout, scheduler: Scheduler): FutureResult[Done] = {
    EitherT(actorRef ? (SendMessage(userId, message, _)))
  }
}
