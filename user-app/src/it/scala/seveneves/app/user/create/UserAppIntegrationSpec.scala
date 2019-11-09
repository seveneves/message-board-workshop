package seveneves.app.user.create

import java.util.UUID

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.IntegrationPatience

class UserAppIntegrationSpec extends WordSpec with Eventually with IntegrationPatience with Matchers {
  "board-message-app" must {
    "be successful" when {
      "creating user" in {
        val name = UUID.randomUUID().toString
        val email = UUID.randomUUID().toString
        val response = UserAppClient.create(name, email)
        response.isSuccess shouldBe true
      }
    }
  }
}
