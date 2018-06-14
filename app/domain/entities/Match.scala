package domain.entities

import javax.persistence._
import play.api.libs.json._

@Entity
@Table(name = "Game")
class Match {
  @Id
  @GeneratedValue
  var Id: Int = 0

  @OneToOne
  var team1: Team = _
  @OneToOne
  var team2: Team = _


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
