package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._

import scala.collection.JavaConverters._

@Entity
@NamedQueries(Array(
  new NamedQuery(name = "Player.FindPlayerByName",
    query = "from Player p where p.name =:name"),
  new NamedQuery(name = "Player.DeleteAllIn",
    query = "delete from Player p where p.name in :ids")
))
class Player {
  @Id
  var name: String = ""
  @ElementCollection(targetClass = classOf[String])
  var battleTag: util.List[String] = _

  def this(name: String) = {
    this()
    this.name = name
  }

  def this(name: String, battleTag: util.List[String]) = {
    this()
    this.name = name
    this.battleTag = battleTag
  }
}

object Player {

  implicit object SearchFormat extends Format[Player] {
    def reads(json: JsValue): JsResult[Player] = JsSuccess(new Player(
      (json \ "name").as[String],
      (json \ "battleTag").as[List[String]].asJava
    ))

    def writes(player: Player): JsObject = Json.obj(
      "name" -> player.name,
      "battleTag" -> player.battleTag.asScala
    )
  }

}
