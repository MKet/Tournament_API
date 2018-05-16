package entities

import java.util

import javax.persistence.{Entity, Id}

@Entity
class User {

  @Id
  var username: String = _
  var password: String = _
  var battleTags: util.ArrayList[String] = new util.ArrayList[String]()
  
}
