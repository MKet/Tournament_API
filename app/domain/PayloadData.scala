package domain

import play.api.libs.json.{Json, OFormat}

case class PayloadData (sub: ClaimUser)

object PayloadData {
  implicit val payLoadFormat: OFormat[PayloadData] = Json.format[PayloadData]
}
