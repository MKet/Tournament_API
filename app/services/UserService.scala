package services

import java.io.Closeable

import domain.ClaimUser
import domain.entities.User
import javax.persistence.{EntityManager, EntityTransaction, NoResultException}
import org.mindrot.jbcrypt.BCrypt

trait UserService extends Closeable {
  def find(name: String): User

  def add(user: User)

  def login(user: User): Boolean

  def checkName(user: ClaimUser): Boolean

  def close(): Unit
}

class EntityUserService(manager: EntityManager) extends UserService {

  override def add(user: User): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    val HashedUser = new User(user.username, BCrypt.hashpw(user.password, BCrypt.gensalt()))

    transaction.begin()
    manager.persist(HashedUser)
    transaction.commit()
  }

  override def login(user: User): Boolean = {
    try {
      val userStorage = findUserByName(user.username)
      userStorage != null && checkUsers(user, userStorage)
    }
    catch {
      case _: NoResultException => false
    }
  }

  private def checkUsers(u: User, u2: User) = u.username.equals(u2.username) && BCrypt.checkpw(u.password, u2.password)

  def findUserByName(name: String): User = manager
    .createNamedQuery("User.FindUserByName")
    .setParameter("username", name)
    .getSingleResult
    .asInstanceOf[User]

  def close(): Unit = manager.close()

  override def checkName(user: ClaimUser): Boolean = find(user.name) != null

  override def find(name: String): User = findUserByName(name)
}
