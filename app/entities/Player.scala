package entities

import java.util

import javax.persistence._
import play.api.libs.json.{Format, JsArray, JsObject, JsResult, JsString, JsSuccess, JsValue, Json}

@Entity
class Player {
  def this(name: String,
            battleTag: List[String]) = {
    this()
    this.name = name
    this.battleTag = battleTag
  }
  @Id
  var name: String = String
  var battleTag: List[String] = util.ArrayList[String]

  @ManyToMany(mappedBy="players")
  var teams: List[Team] = util.ArrayList[Team]
}

object Player {
  implicit object SearchFormat extends Format[Player] {
    def reads(json: JsValue): JsResult[Player] = JsSuccess(new Player(
      (json \ "name").as[String],
      (json \ "battleTag").as[List[String]]
    ))

    def writes(s: Player): JsValue = JsObject(Seq(
      "name" -> JsString(s.name),
      "battleTag" -> JsArray(s.battleTag.map(Json.toJson(_)))
    ))
  }
}
