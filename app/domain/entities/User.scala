package entities

import javax.persistence._
import play.api.libs.json.Reads._
import play.api.libs.json._

@Entity
@NamedQuery(name = "User.FindUserByName", query = "from User u where u.username =:username")
class User {
  @Id
  var username: String = ""
  var password: String = ""

  def this(username: String,
           password: String) = {
    this()
    this.username = username
    this.password = password
  }
}

object User {

  implicit object SearchFormat extends Format[User] {
    def reads(json: JsValue): JsResult[User] = JsSuccess(new User(
      (json \ "username").as[String],
      (json \ "password").as[String]
    ))

    def writes(s: User): JsValue = JsObject(Seq(
      "username" -> JsString(s.username),
      "password" -> JsString(s.password)
    ))
  }

}

