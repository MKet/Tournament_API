package controllers

import domain.ClaimUser
import domain.entities._
import factories.ServiceFactory
import javax.inject.Inject
import play.api.mvc.Action

import scala.concurrent.{ExecutionContext, Future}

class TournamentController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {

  def create: Action[Tournament] = AuthenticatedAction.async(parse.json[Tournament]) {
    implicit request => {
      val tournamentService = sf.TournamentService
      val userService = sf.UserService
      val tournament = request.body

      Future(Ok("Tournament created"))
    }
  }

  def delete: Action[List[Int]] = AuthenticatedAction.async(parse.json[List[Int]]) {
    implicit request => {
      val tournamentService = sf.TournamentService
      tournamentService delete request.body
      Future(Ok("Test"))
    }
  }
}


