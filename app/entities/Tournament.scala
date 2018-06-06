package entities

import java.util

import javax.persistence._
import play.api.libs.json._


@Entity
class Tournament {

  @Id
  @GeneratedValue
  var Id: Int = 0
  @ManyToOne
  var owner: User = _
  @OneToMany
  var teams: util.List[Team] = _
  @OneToMany
  var matches: util.List[Match] = _

  def this(Id: Int) = {
    this()
    this.Id = Id

  }
}

object Tournament {

  implicit object SearchFormat extends Format[Tournament] {
    def reads(json: JsValue): JsResult[Tournament] = JsSuccess(new Tournament(
      (json \ "Id").as[Int]
    ))

    def writes(s: Tournament): JsValue = JsObject(Seq(
      "Id" -> JsNumber(s.Id)
    ))
  }

}
