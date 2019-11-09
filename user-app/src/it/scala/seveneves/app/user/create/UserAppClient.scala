package seveneves.app.user.create

import scalaj.http.Http
import scalaj.http.HttpResponse

object UserAppClient {

  import HttpOps._

  private val connection = System.getProperty("user-app_1_8080")

  def create(name: String, email: String): HttpResponse[String] = {
    Http(s"http://$connection/api/user")
      .postData(s"""{"name": "$name", "email": "$email"}""")
      .header("Content-Type", "application/json")
      .logAndCallAsString()
  }

  def retrieve(userId: String): HttpResponse[String] = {
    Http(s"http://$connection/api/user/$userId")
      .logAndCallAsString()
  }
}
