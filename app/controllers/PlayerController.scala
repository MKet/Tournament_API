package controllers

import domain.entities.Player
import factories.ServiceFactory
import javax.inject.Inject
import play.api.mvc.Action

import scala.concurrent.{ExecutionContext, Future}

class PlayerController @Inject()(cc: SecuredControllerComponents, sf: ServiceFactory)(implicit ec: ExecutionContext) extends SecuredController(cc) {
  def create: Action[Player] = AuthenticatedAction.async(parse.json[Player]) {
    implicit request => {
      val ps = sf.PlayerService
      try {
          ps add request.body
        }
        finally {
          ps.close()
        }
      }
    Future(Ok("player created"))
  }

  def delete: Action[List[String]] = AuthenticatedAction.async(parse.json[List[String]]) {
    implicit request => {
      val ps = sf.PlayerService
      try {
        ps delete request.body
      }
      finally {
        ps.close()
      }
      Future(Ok("Player deleted"))
    }
  }
}
