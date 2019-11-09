package seveneves.app.user.create

object ApiModel {

  case class CreateUserRequest(
    name: String,
    email: String,
  )

  case class CreateUserResponse(
    userId: String,
  )

  case class GetUserResponse(
    name: String,
    email: String,
  )

  case class Error(error: String)
}
