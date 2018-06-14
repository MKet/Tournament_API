package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._

import scala.collection.JavaConverters._


@Entity
@NamedQuery(name = "Tournament.DeleteAllIn", query = "delete from Tournament t where t.Id in :ids")
class Tournament {

  @Id
  @GeneratedValue
  var Id: Int = 0
  var name: String = _
  @OneToMany
  var teams: util.List[Team] = _
  @OneToMany
  var matches: util.List[Match] = _

  def this(name: String) = {
    this()
    this.name = name
  }

  def this(name: String, teams: util.List[Team], matches: util.List[Match]) = {
    this(name)
    this.teams = teams
    this.matches = matches
  }
}

object Tournament {

  implicit object SearchFormat extends Format[Tournament] {
    def reads(json: JsValue): JsResult[Tournament] = JsSuccess(new Tournament(
      (json \ "name").as[String],
      (json \ "teams").as[List[Team]].asJava,
      (json \ "matches").as[List[Match]].asJava
    ))

    def writes(s: Tournament): JsValue = JsObject(Seq(
      "id" -> JsNumber(s.Id),
      "name" -> JsString(s.name),
      "teams" -> Json.toJson(s.teams.asScala),
      "matches" -> Json.toJson(s.matches.asScala)
    ))
  }

}

