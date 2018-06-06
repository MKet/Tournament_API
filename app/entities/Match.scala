package entities

import javax.persistence.{Entity, GeneratedValue, Id}
import play.api.libs.json._

@Entity
class Match {
  @Id
  @GeneratedValue
  var Id: Int = 0

  def this(Id: Int) = {
    this()
    this.Id = Id
  }
}

object Match {

  implicit object SearchFormat extends Format[Match] {
    def reads(json: JsValue): JsResult[Match] = JsSuccess(new Match(
      (json \ "Id").as[Int]
    ))

    def writes(s: Match): JsValue = JsObject(Seq(
      "Id" -> JsNumber(s.Id),
    ))
  }

}
