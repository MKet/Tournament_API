package entities

import java.util

import javax.persistence._
import play.api.libs.json._

@Entity
class Tournament {
  def this(Id: Int) = {
    this()
    this.Id = Id
  }
  @Id
  @GeneratedValue
  var Id: Int = 0

  @ManyToOne
  var owner: User = _

  @OneToMany
  var teams: util.List[Team] = util.ArrayList[Team]
  @OneToMany
  var matches: util.List[Match] = util.ArrayList[Match]
}

object Tournament {
  implicit object SearchFormat extends Format[Tournament] {
    def reads(json: JsValue): JsResult[Tournament] = JsSuccess(new Tournament(
      (json \ "Id").as[Integer],
      (json \ "owner").as[User]
    ))

    def writes(s: Tournament): JsValue = JsObject(Seq(
      "Id" -> JsNumber(s.Id),
      "owner" -> Json.toJson(s.owner)
    ))
  }
}
