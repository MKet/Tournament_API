package controllers

import authentikat.jwt.JwtClaimsSet
import domain.ClaimUser
import domain.entities.User
import factories.ServiceFactory
import javax.inject._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc._
import services.JwtService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def create: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.UserService
      service add request.body

      Future(Ok("User created"))
    }
  }

  def login: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.UserService
     if (service login request.body) {
       val cUser =  ClaimUser(request.body.username)
       val token =  JwtService.createToken(JwtClaimsSet(Map("sub" -> cUser)))
       Future(Ok.withHeaders(("Authorization", "Bearer "+token)))
     }
      else
        Future(Unauthorized)
    }
  }
}
