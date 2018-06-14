package controllers

import domain.ClaimUser
import domain.entities.User
import factories.ServiceFactory
import javax.inject._
import play.api.mvc._
import pdi.jwt._
import pdi.jwt.JwtSession._
import play.api.libs.json.Json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def create: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.getUserService
      service add request.body
      Future(Ok("User created"))
    }
  }

  def login: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.getUserService
        if (service login request.body) {
          var session = JwtSession()
          session = session ++ (("IssuedAt", session.claim.issuedNow.toJson), ("sub", ClaimUser(request.body.username)))
          Future(Ok("Success").withJwtSession(session))
        }
         else
          Future(Unauthorized)
    }
  }
}
