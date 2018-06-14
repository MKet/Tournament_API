package controllers

import javax.inject.Inject
import domain.ClaimUser
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Results._
import play.api.mvc._
import services._

import scala.concurrent.{ExecutionContext, Future}

case class PayloadClass (sub: ClaimUser)

object  PayloadClass {
  implicit val payLoadFormat: OFormat[PayloadClass] = Json.format[PayloadClass]
}

class AuthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    var jwtToken = request.headers.get("Authorization").getOrElse("")

    if (jwtToken matches "^Bearer .*") {
      jwtToken = jwtToken.replaceFirst("^Bearer ", "")
      if (JwtService.isValidToken(jwtToken)) {
        JwtService.decodePayload(jwtToken).fold {
          Future(Unauthorized("Invalid credential"))
        } { payload =>
          block(new AuthenticatedRequest(request))
        }
      } else {
        Future(Unauthorized("Invalid credential"))
      }
    } else
      Future(Unauthorized("Invalid credential"))

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