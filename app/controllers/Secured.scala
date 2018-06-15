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

case class PayloadData (sub: ClaimUser)

object PayloadData {
  implicit val payLoadFormat: OFormat[PayloadData] = Json.format[PayloadData]
}

class AuthenticatedRequest[A](payload: PayloadData, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    var jwtToken = request.headers.get("Authorization").getOrElse("")

    if (jwtToken matches "^Bearer .*") {
      jwtToken = jwtToken.replaceFirst("^Bearer ", "")
      if (JwtService.isValidToken(jwtToken)) {
        JwtService.decodePayload(jwtToken).fold {
          System.out.println("Jwttoken misformed")
          Future(Unauthorized("Invalid credential"))
        } { jwt =>
          Json.parse(jwt).validate[PayloadData].fold(
            invalid = { _ =>
              System.out.println("Payload data misformed")
              Future(Unauthorized("Invalid credential"))
          }, valid = { payload: PayloadData =>
            block(new AuthenticatedRequest(payload, request))
          })
        }
      } else {
        System.out.println("Jwttoken invalid")
        Future(Unauthorized("Invalid credential"))
      }
    } else {
      System.out.println("No Bearer prefix")
      Future(Unauthorized("Invalid credential"))
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