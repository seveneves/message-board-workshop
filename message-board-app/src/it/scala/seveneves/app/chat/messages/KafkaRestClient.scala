package seveneves.app.chat.messages

import java.util.Base64

import scalaj.http.Http
import seveneves.app.user.create.proto.UserCreated.UserCreated

object KafkaRestClient {
  private val kafkaRestConnection = System.getProperty("kafka-rest_1_8082")
  import HttpOps._

  def send(userCreated: UserCreated): Unit = {
    Http(s"http://$kafkaRestConnection/topics/user-app-user-created")
      .postData(s"""{"records":[{"key": "${userCreated.id}","value": "${Base64.getEncoder.encodeToString(userCreated.toByteArray)}"}]}""")
      .header("Content-Type", "application/vnd.kafka.binary.v2+json")
      .logAndCallAsString()
  }
}
