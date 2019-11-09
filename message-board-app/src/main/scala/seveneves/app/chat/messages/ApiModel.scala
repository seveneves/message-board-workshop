package seveneves.app.chat.messages;

object ApiModel {

  case class SendMessage(
    userId: String,
    message: String,
  )

  case class MessagesResponse(messages: Seq[String])

  case class Error(error: String)
}
