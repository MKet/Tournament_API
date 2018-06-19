package controllers

import domain.entities.User
import factories.ServiceFactory
import javax.inject._
import play.api.mvc._
import services.JwtService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def create: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.UserService
      try {
        val service = sf.UserService
        service add request.body
        service.close()
        Future(Ok("User created"))
      }
      finally {
        service.close()
      }
    }
  }

  def login: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.UserService
      try {
        if (service login request.body) {
          Future(JwtService.addAuthHeader(Ok, request.body.username))
        }
        else
          Future(Unauthorized)
      }
      finally {
        service.close()
      }
    }
  }
}
