package domain

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

case class PayloadData (sub: ClaimUser, exp: Long, nbf: Long = DateTime.now().getMillis / 1000)

object PayloadData {
  implicit val payLoadFormat: OFormat[PayloadData] = Json.format[PayloadData]
}
