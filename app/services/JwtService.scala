package services

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import domain.PayloadData
import org.joda.time.DateTime
import play.api.mvc.{Request, Result}

object JwtService {
  val JwtSecretKey = "secretKey"
  val JwtSecretAlgorithm = "HS256"
  val MaxAge: Int = 20

  def getFromRequest(request: Request[Any]): String = request.headers.get("Authorization").get.split(' ')(1)

  def isValidPayload(payloadData: PayloadData): Boolean = isValidPayload(payloadData, DateTime.now().getMillis / 1000)

  def isValidPayload(payloadData: PayloadData, currentTime: Long): Boolean =
    payloadData.exp >= currentTime && payloadData.nbf < currentTime

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(_, claimsSet, _) => Option(claimsSet.asJsonString)
      case _ => None
    }

  def addAuthHeader(result: Result, sub: String): Result = {
    val payloadData = PayloadData(sub, DateTime.now().plusMinutes(MaxAge).getMillis / 1000)
    val token = createToken(JwtClaimsSet(toMap(payloadData)))

    result.withHeaders(("Authorization", "Bearer " + token))
  }

  def createToken(claimsSet: JwtClaimsSet): String = {
    val header = JwtHeader(JwtSecretAlgorithm)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def toMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }
}
