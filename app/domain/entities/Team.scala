package entities

import java.util

import javax.persistence._
import play.api.libs.json._

@Entity
class Team {
  @Id
  @GeneratedValue
  var Id: Int = 0
  @ManyToMany
  var players: util.List[Player] = _

  def this(Id: Int) = {
    this()
    this.Id = Id
  }
}

object Team {

  implicit object SearchFormat extends Format[Team] {
    def reads(json: JsValue): JsResult[Team] = JsSuccess(new Team(
      (json \ "Id").as[Int]
    ))

    def writes(s: Team): JsValue = JsObject(Seq(
      "Id" -> JsNumber(s.Id)
    ))
  }

}
