package services

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import domain.{ClaimUser, PayloadData}
import org.joda.time.DateTime
import play.api.mvc.{Request, Result}

object JwtService {
  val JwtSecretKey = "secretKey"
  val JwtSecretAlgo = "HS256"
  val MaxAge: Int = 20

  def createToken(claimsSet: JwtClaimsSet): String = {
    val header = JwtHeader(JwtSecretAlgo)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def getFromRequest(request: Request[Any]): String = request.headers.get("Authorization").get.split(' ')(1)

  def isValidPayload(payloadData: PayloadData): Boolean = isValidPayload(payloadData, DateTime.now().getMillis / 1000)

  def isValidPayload(payloadData: PayloadData, currentTime: Long): Boolean =
      payloadData.exp >= currentTime && payloadData.nbf < currentTime
  
  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _                                          => None
    }

  def toMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }

  def addAuthHeader(result: Result, sub: String): Result = {
    val cUser =  ClaimUser(sub)
    val payloadData = PayloadData(cUser, DateTime.now().plusMinutes(MaxAge).getMillis / 1000)
    val token =  createToken(JwtClaimsSet(toMap(payloadData)))

     result.withHeaders(("Authorization", "Bearer "+token))
  }
}
