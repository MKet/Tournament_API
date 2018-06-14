package domain

import play.api.libs.json.{Json, OFormat}

case class ClaimUser(name: String) {}

object ClaimUser {
  implicit val userFormat: OFormat[ClaimUser] = Json.format[ClaimUser]
}
