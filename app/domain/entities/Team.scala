package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._

@Entity
class Team {
  @Id
  @GeneratedValue
  var Id: Int = 0

  var name: String = _
  @ManyToMany
  var players: util.List[Player] = _

  def this(name: String) = {
    this()
    this.name = name
  }
}

object Team {

  implicit object SearchFormat extends Format[Team] {
    def reads(json: JsValue): JsResult[Team] = JsSuccess(new Team(
      (json \ "name").as[String]
    ))

    def writes(s: Team): JsValue = JsObject(Seq(
      "name" -> JsString(s.name)
    ))
  }

}
