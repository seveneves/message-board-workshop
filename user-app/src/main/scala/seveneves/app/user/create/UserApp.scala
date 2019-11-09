package seveneves.app.user.create

import akka.actor
import akka.actor.typed.Scheduler
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

object UserApp extends App {
  private val logger = LoggerFactory.getLogger(this.getClass)
  implicit private val actorSystem: actor.ActorSystem = actor.ActorSystem()
  implicit private val timeout: Timeout = 10.seconds
  implicit private val kafkaExt: KafkaProducer = new KafkaProducer()
  private val users = actorSystem.spawn(Users.storeUsersBehaviour(Map.empty), "Users")
  implicit private val scheduler: Scheduler = actorSystem.scheduler.toTyped
  import actorSystem.dispatcher

  private val userRoutes = new UserRoutes(users)

  logger.info("Starting up http server")
  Http().bindAndHandle(userRoutes.createUser, "0.0.0.0", 8080).onComplete {
    case Success(binding) => logger.info(s"Http Server Bound to ${binding.localAddress}")
    case Failure(ex) => logger.error(s"Http Server could not bind to port 8080", ex)
  }
}
