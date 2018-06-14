package controllers

import domain.ClaimUser
import domain.entities._
import factories.ServiceFactory
import javax.inject.Inject
import pdi.jwt.JwtSession._
import play.api.mvc.Action

import scala.concurrent.{ExecutionContext, Future}

class TournamentController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {


  def create: Action[Tournament] = Action.async(parse.json[Tournament]) {
    implicit request => {
      val tournamentService = sf.getTournamentService
      val userService = sf.getUserService
      val tournament = request.body
      val user = userService.find(request.jwtSession.getAs[ClaimUser]("sub").get.name)

      tournamentService.add(tournament, user)
      Future(Ok("Tournament created"))
    }
  }

  def delete: Action[List[Int]] = AuthenticatedAction.async(parse.json[List[Int]]) {
    implicit request => {
      val tournamentService = sf.getTournamentService
      tournamentService delete request.body
      Future(Ok("Test"))
    }
  }
}


