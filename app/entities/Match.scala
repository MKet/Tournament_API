package entities

import javax.persistence.{GeneratedValue, Id, ManyToMany, OneToMany}
import play.api.libs.json._

class Match {
  def this(Id: Int, team1: Team, team2: Team ) = {
    this()
    this.Id = Id
    this.team1 = team1
    this.team2 = team2
  }
  @Id
  @GeneratedValue
  var Id : Int = 0

  @OneToMany
  var team1: Team = _
  @OneToMany
  var team2: Team = _
}

object Match {
  implicit object SearchFormat extends Format[Match] {
    def reads(json: JsValue): JsResult[Match] = JsSuccess(new Match(
      (json \ "Id").as[Integer],
      (json \ "team1").as[Team],
      (json \ "team2").as[Team]
    ))

    def writes(s: Match): JsValue = JsObject(Seq(
      "Id" -> JsNumber(s.Id),
      "team1" -> Json.toJson(s.team1),
      "team2" -> Json.toJson(s.team2)
    ))
  }
}
