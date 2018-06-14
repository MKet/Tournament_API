package controllers

import domain.ClaimUser
import domain.entities.{Player, Tournament}
import factories.ServiceFactory
import javax.inject.Inject
import play.api.mvc.Action

import scala.concurrent.{ExecutionContext, Future}

class PlayerController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {
  def create: Action[Player] = AuthenticatedAction.async(parse.json[Player]) {
    implicit request => {

      Future(Ok("player created"))
    }
  }

  def delete: Action[List[Int]] = AuthenticatedAction.async(parse.json[List[Int]]) {
    implicit request => {

      Future(Ok("Test"))
    }
  }
}
