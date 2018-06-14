package controllers

import java.util

import domain.entities._
import factories.ServiceFactory
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class TournamentController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {


  def create: Action[Tournament] = AuthenticatedAction.async(parse.json[Tournament]) {
    implicit request => {
      try {
        val tournamentService = sf.getTournamentService
          tournamentService add request.body
          Future(Ok("Tournament created"))
      }
      catch {
        case _: NoSuchElementException => Future(Unauthorized)
      }
    }
  }

  def delete: Action[AnyContent] = AuthenticatedAction.async {
    implicit request => {
      Future(Ok("Test"))
    }
  }
}


