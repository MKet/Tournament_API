package controllers

import entities.User
import factories.ServiceFactory
import javax.inject._
import play.api.mvc._


class UserController @Inject()(cc: ControllerComponents, sf: ServiceFactory) extends AbstractController(cc){
  def create = Action(parse.json[User]) {
    implicit request => {
      sf.getUserService addUser request.body
      Ok("User created")
    }
  }

  def login = Action(parse.json[User]) {
    implicit request => {
      val token : String = sf.getUserService login request.body
      if (token != null)
        Ok(token)
      else
        Unauthorized
    }
  }
}
