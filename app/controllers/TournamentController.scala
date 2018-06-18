package controllers

import domain.entities._
import factories.ServiceFactory
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

class TournamentController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {

  def create: Action[Tournament] = AuthenticatedAction.async(parse.json[Tournament]) {
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService
      val userService = sf.UserService
      val tournament = request.body
      val user = userService find authRequest.payload.sub.name

      tournamentService.add(tournament, user)
      Future(Ok("Tournament created"))
    }
  }

  def get: Action[AnyContent] = AuthenticatedAction.async(parse.anyContent) {
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService

      val tournamentList = tournamentService getAllOwnedBy authRequest.payload.sub.name
      Future(Ok(Json.toJson(tournamentList).toString))
    }
  }

  def delete: Action[List[Int]] = AuthenticatedAction.async(parse.json[List[Int]]){
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService

      tournamentService.delete(request.body, authRequest.payload.sub.name)
      Future(Ok("Tournaments deleted"))
    }
  }
}


