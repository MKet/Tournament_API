package controllers

import java.util.regex.Pattern

import domain.PayloadData
import javax.inject.Inject
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc._
import services._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val payload: PayloadData, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val jwtToken = request.headers.get("Authorization").getOrElse("")

      val m = Pattern.compile("^Bearer ([a-zA-Z0-9\\-_]+?\\.[a-zA-Z0-9\\-_]+?\\.([a-zA-Z0-9\\-_]+))?$").matcher(jwtToken)
      if (m.find() && JwtService.isValidToken(m.group(1))) {
        JwtService.extractPayload(m.group(1)) match {
          case Some(payload) =>
            block(new AuthenticatedRequest(payload, request))
          case _ =>
            Future(Unauthorized)
        }
      } else {
        System.out.println("Jwt-token invalid")
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