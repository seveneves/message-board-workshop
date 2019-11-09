package seveneves.app.chat.messages

import scalaj.http.Http
import scalaj.http.HttpResponse

object BoardMessageAppClient {

  import HttpOps._

  private val connection = System.getProperty("message-board-app_1_8080")

  def send(user: String, message: String): HttpResponse[String] = {
    Http(s"http://$connection/api/chat")
      .put(s"""{"userId": "$user", "message": "$message"}""")
      .header("Content-Type", "application/json")
      .logAndCallAsString()
  }

  def retrieve(user: String) = {
    Http(s"http://$connection/api/chat/$user")
      .logAndCallAsString()
  }
}
