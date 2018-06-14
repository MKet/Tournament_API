package domain.entities

import java.util

import javax.persistence._
import play.api.libs.json._


@Entity
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

  def this(name: String, owner: User) = {
    this(name)
  }
}

object Tournament {

  implicit object SearchFormat extends Format[Tournament] {
    def reads(json: JsValue): JsResult[Tournament] = JsSuccess(new Tournament(
      (json \ "name").as[String]
    ))

    def writes(s: Tournament): JsValue = JsObject(Seq(
      "name" -> JsString(s.name)
    ))
  }

}

