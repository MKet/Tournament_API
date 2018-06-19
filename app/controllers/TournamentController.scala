package controllers

import domain.entities._
import factories.ServiceFactory
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class TournamentController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {

  def create: Action[Tournament] = AuthenticatedAction.async(parse.json[Tournament]) {
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService
      val userService = sf.UserService
      val tournament = request.body

      try {
        val user = userService find authRequest.payload.sub
        tournamentService.add(tournament, user)
      } finally {
        tournamentService.close()
        userService.close()
      }
      Future(Ok("Tournament created"))
    }
  }

  def get: Action[AnyContent] = AuthenticatedAction.async(parse.anyContent) {
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService

      try {
        val tournamentList = tournamentService getAllOwnedBy authRequest.payload.sub
        Future(Ok(Json.toJson(tournamentList).toString))
      }
      finally {
        tournamentService.close()
      }
    }
  }

  def delete: Action[List[Int]] = AuthenticatedAction.async(parse.json[List[Int]]) {
    implicit request => {
      val authRequest = request.asInstanceOf[AuthenticatedRequest[Tournament]]
      val tournamentService = sf.TournamentService

      tournamentService.delete(request.body, authRequest.payload.sub)
      tournamentService.close()
      Future(Ok("Tournaments deleted"))
    }
  }
}


