package seveneves.app.chat.messages

import org.slf4j.LoggerFactory
import scalaj.http.HttpRequest
import scalaj.http.HttpResponse

object HttpOps {
  private val logger = LoggerFactory.getLogger(this.getClass)

  implicit class RequestOps(val http: HttpRequest) extends AnyVal {

    def logAndCallAsString(): HttpResponse[String] = {
      logger.info(s"Calling ${http.method}:${http.url}")
      val response = http.asString
      logger.info(s"Received ${response.code}:\n${response.body}")
      response
    }
  }
}
