package controllers

import entities.User
import factories.ServiceFactory
import javax.inject._
import play.api.mvc._


class UserController @Inject()(cc: ControllerComponents, sf: ServiceFactory) extends AbstractController(cc) {
  def create: Action[User] = Action(parse.json[User]) {
    implicit request => {
      val service = sf.getUserService
      try {
        service addUser request.body
      } finally {
        service.close()
      }
      Ok("User created")
    }
  }

  def login: Action[User] = Action(parse.json[User]) {
    implicit request => {
      val service = sf.getUserService
      try {
        val token: String = service login request.body
        if (token != null)
          Ok(token)
        else
          Unauthorized
      } finally {
        service.close()
      }
    }
  }
}
