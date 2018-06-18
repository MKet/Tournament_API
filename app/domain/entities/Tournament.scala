package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._

import scala.collection.JavaConverters._


@Entity
@NamedQueries(Array(
  new NamedQuery(name = "Tournament.DeleteAllIn", query = "delete from Tournament t where t.Id in :ids and t.owner.username = :owner"),
  new NamedQuery(name = "Tournament.GetAllOwnerBy", query = "From Tournament t where t.owner.username= :owner")
))
class Tournament {

  @Id
  @GeneratedValue
  val Id: Int = 0
  var name: String = _
  @OneToMany
  var teams: util.List[Team] = _
  @OneToMany
  var matches: util.List[Match] = _
  @ManyToOne
  var owner: User = _

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

    def writes(tournament: Tournament): JsObject =
      Json.obj(
        "id" -> tournament.Id,
        "name" -> tournament.name,
        "teams" -> tournament.teams.asScala,
        "matches" -> tournament.matches.asScala
      )
  }

}

