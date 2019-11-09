package seveneves.app.chat.messages

import akka.actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Scheduler
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

object MessageBoardApp extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)

  implicit private val actorSystem: actor.ActorSystem = actor.ActorSystem()
  implicit private val timeout: Timeout = 10.seconds
  implicit private val messageProcessor: ActorRef[Messages.Command] = actorSystem.spawn(Messages.messageProcessor(), "Messages")
  implicit private val scheduler: Scheduler = actorSystem.scheduler.toTyped
  import actorSystem.dispatcher

  private val messageRoutes = new MessageRoutes()
  private val control = KafkaConsumer.subscribe(messageProcessor)

  actorSystem.registerOnTermination {
    control.shutdown()
  }

  logger.info("Starting up http server")
  Http().bindAndHandle(messageRoutes.api, "0.0.0.0", 8080).onComplete {
    case Success(binding) => logger.info(s"Http Server Bound to ${binding.localAddress}")
    case Failure(ex) => logger.error(s"Http Server could not bind to port 8080", ex)
  }
}
