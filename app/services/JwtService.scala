package services

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import play.api.mvc.Request

object JwtService {
  val JwtSecretKey = "secretKey"
  val JwtSecretAlgo = "HS256"

  def createToken(claimsSet: JwtClaimsSet): String = {
    val header = JwtHeader(JwtSecretAlgo)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def getFromRequest(request: Request[Any]): String = request.headers.get("Authorization").get.split(' ')(1)

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _                                          => None
    }
}
