package controllers

import authentikat.jwt.JwtClaimsSet
import domain.{ClaimUser, PayloadData}
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
      service add request.body

      Future(Ok("User created"))
    }
  }

  def login: Action[User] = Action.async(parse.json[User]) {
    implicit request => {
      val service = sf.UserService
     if (service login request.body) {
       val cUser =  ClaimUser(request.body.username)
       val payloadData = PayloadData(cUser)
       val token =  JwtService.createToken(JwtClaimsSet(toMap(payloadData)))

       Future(Ok.withHeaders(("Authorization", "Bearer "+token)))
     }
      else
        Future(Unauthorized)
    }
  }

  def toMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }
}
