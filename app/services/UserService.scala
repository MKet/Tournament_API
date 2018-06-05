package services

import entities.User
import javax.inject.Singleton
import javax.persistence.{EntityManager, EntityTransaction, NoResultException}
import org.mindrot.jbcrypt.BCrypt
import pdi.jwt.JwtSession

trait UserService {
  def addUser(user: User)
  def login(user: User) : String
}

@Singleton
class EntityUserService(manager: EntityManager) extends  UserService {

  override def addUser(user: User) = {
    val transaction : EntityTransaction = manager.getTransaction

    val HashedUser = new User(user.username, BCrypt.hashpw(user.password, BCrypt.gensalt()))

    transaction.begin()
    manager.persist(HashedUser)
    transaction.commit()
  }

  override def login(user: User): String = {
    try {
      val userStorage = findUserByName(user)
      if (userStorage != null && checkUsers(user, userStorage)) {
        var session = JwtSession()
        session = session ++ (("IssuedAt", session.claim.issuedNow.toJson), ("Iss", "Tournament_API"), ("sub", user.username))
        session.serialize
      }
      else null
    }
    catch {
      case _: NoResultException => null
    }
  }

  private def checkUsers(u: User, u2: User) = u.username.equals(u2.username) && BCrypt.checkpw(u.password, u2.password)

  private def findUserByName(u: User) = manager
      .createNamedQuery("User.FindUserByName")
      .setParameter("username", u.username)
      .getSingleResult
      .asInstanceOf[User]
}
