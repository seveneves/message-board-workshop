package seveneves.app.chat.messages

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.kafka.CommitterSettings
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Committer
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import seveneves.app.user.create.proto.UserCreated.UserCreated

object KafkaConsumer {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def subscribe(messageProcessor: ActorRef[Messages.InitUser])(implicit actorSystem: ActorSystem): Consumer.Control = {

    val consumerSettings = ConsumerSettings(actorSystem, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers("kafka:9092")
      .withGroupId("message-board")

    val committerSettings = CommitterSettings(actorSystem)

    Consumer
      .committableSource(consumerSettings, Subscriptions.topics("user-app-user-created"))
      .map { commitRecord =>
        commitRecord.committableOffset -> UserCreated.parseFrom(commitRecord.record.value())
      }
      .alsoTo(Sink.foreach {
        case (_, user) =>
          logger.info(s"User is initiated ${user.id}")
          Messages.initUser(user.id)(messageProcessor)
      })
      .map(_._1)
      .to(Committer.sink(committerSettings))
      .run()
  }
}
