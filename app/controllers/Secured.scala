package controllers

import authentikat.jwt.JwtClaimsSet
import domain.{ClaimUser, PayloadData}
import javax.inject.Inject
import org.joda.time.{DateTime, Seconds}
import play.api.Application
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import services._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AuthenticatedRequest[A](val payload: PayloadData, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    var jwtToken = request.headers.get("Authorization").getOrElse("")

    if (jwtToken matches "^Bearer .*") {
      jwtToken = jwtToken.replaceFirst("^Bearer ", "")
      if (JwtService.isValidToken(jwtToken)) {
        JwtService.decodePayload(jwtToken).fold {
          System.out.println("Jwttoken misformed")
          Future(Unauthorized("Invalid credentials"))
        } { jwt =>
          Json.parse(jwt).validate[PayloadData].fold(
            invalid = { _ =>
              System.out.println("Payload data misformed")
              Future(Unauthorized("Invalid credentials"))
          }, valid = { payload: PayloadData =>
              if (JwtService.isValidPayload(payload))
                block(new AuthenticatedRequest(payload, request)).transform({
                  result: Try[Result] =>
                    if (result.isSuccess) {
                      Try(JwtService.addAuthHeader(result.get, payload.sub.name))
                    } else {
                      result
                    }
                })
              else
                Future(Unauthorized("Token expired"))
          })
        }
      } else {
        System.out.println("Jwttoken invalid")
        Future(Unauthorized("Invalid credentials"))
      }
    } else {
      System.out.println("No Bearer prefix")
      Future(Unauthorized("Invalid credentials"))
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