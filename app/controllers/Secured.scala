package controllers

import domain.ClaimUser
import javax.inject.Inject
import pdi.jwt.JwtSession._
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val user: ClaimUser, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    System.out.print(request.jwtSession)
    request.jwtSession.getAs[ClaimUser]("sub") match {
      case Some(user) =>
        block(new AuthenticatedRequest[A](user, request)).map(_.refreshJwtSession(request))
      case _ =>
        Future(Unauthorized)
    }
  }
}

case class SecuredControllerComponents @Inject()(
                                                  authenticatedActionBuilder: AuthenticatedActionBuilder,
                                                  actionBuilder: DefaultActionBuilder,
                                                  parsers: PlayBodyParsers,
                                                  messagesApi: MessagesApi,
                                                  langs: Langs,
                                                  fileMimeTypes: FileMimeTypes,
                                                  executionContext: scala.concurrent.ExecutionContext
                                                ) extends ControllerComponents

class SecuredController @Inject()(scc: SecuredControllerComponents) extends AbstractController(scc) {
  def AuthenticatedAction: AuthenticatedActionBuilder = scc.authenticatedActionBuilder
}