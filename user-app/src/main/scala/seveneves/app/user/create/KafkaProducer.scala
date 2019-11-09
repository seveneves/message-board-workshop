package seveneves.app.user.create

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.SourceQueueWithComplete
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import seveneves.app.user.create.proto.UserCreated.UserCreated

import scala.util.Failure
import scala.util.Success

class KafkaProducer(implicit actorSystem: ActorSystem) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val producerSettings = ProducerSettings(actorSystem, new StringSerializer, new ByteArraySerializer)
    .withBootstrapServers("kafka:9092")

  private val queue: SourceQueueWithComplete[UserCreated] =
    Source
      .queue[UserCreated](100, OverflowStrategy.backpressure)
      .map(user => user.id -> user.toByteArray)
      .map { case (key, data) => new ProducerRecord[String, Array[Byte]]("user-app-user-created", key, data) }
      .toMat(Producer.plainSink(producerSettings))(Keep.left)
      .run()

  def pushToKafka(userCreated: UserCreated): Unit = {
    import actorSystem.dispatcher
    queue.offer(userCreated).onComplete {
      case Success(value) =>
        logger.info(s"userCreated is queued as $value")
      case Failure(exception) =>
        logger.error(s"Could not queue user", exception)
    }
  }
}
