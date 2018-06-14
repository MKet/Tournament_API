package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._

@Entity
class Player {
  @Id
  var name: String = ""
  @ElementCollection(targetClass=classOf[String])
  var battleTag: util.List[String] = _
  @ManyToMany(mappedBy = "players")
  var teams: util.List[Team] = _

  def this(name: String) = {
    this()
    this.name = name
  }
}

object Player {

  implicit object SearchFormat extends Format[Player] {
    def reads(json: JsValue): JsResult[Player] = JsSuccess(new Player(
      (json \ "name").as[String]
    ))

    def writes(s: Player): JsValue = JsObject(Seq(
      "name" -> JsString(s.name)
    ))
  }

}
