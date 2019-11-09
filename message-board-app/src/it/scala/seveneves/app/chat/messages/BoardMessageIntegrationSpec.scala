package seveneves.app.chat.messages

import java.util.UUID

import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.Matchers
import org.scalatest.WordSpec
import seveneves.app.user.create.proto.UserCreated.UserCreated

class BoardMessageIntegrationSpec extends WordSpec with Eventually with IntegrationPatience with Matchers {
  "board-message-app" must {

    "report user is not found" when {
      "sending messages" in {
        val response = BoardMessageAppClient.send(UUID.randomUUID().toString, "message")
        response.isError shouldBe true
        response.body should include("Not found")
      }

      "retrieving messages" in {
        val response = BoardMessageAppClient.send(UUID.randomUUID().toString, "message")
        response.isError shouldBe true
        response.body should include("Not found")
      }
    }

    "be successful with initiated user" when {
      "sending messages" in {
        val userId = UUID.randomUUID().toString
        eventually {
          KafkaRestClient.send(UserCreated(id = userId, name = "Test", email = "some@test.me"))
          val response = BoardMessageAppClient.send(userId, "message")
          response.isSuccess shouldBe true
        }
      }

      "retrieve messages" in {
        val userId = UUID.randomUUID().toString
        val message = UUID.randomUUID().toString
        eventually {
          KafkaRestClient.send(UserCreated(id = userId, name = "Test", email = "some@test.me"))
          BoardMessageAppClient.send(userId, message)
          val response = BoardMessageAppClient.retrieve(userId)
          response.body should include(message)
          response.isSuccess shouldBe true
        }
      }
    }
  }
}
