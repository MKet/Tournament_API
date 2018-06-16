package services

import domain.entities.{Player, Team, User}
import javax.persistence.{EntityManager, EntityTransaction, NoResultException}
import org.mindrot.jbcrypt.BCrypt

import scala.collection.JavaConverters._

trait PlayerService {
  def add(p: Player): Unit
  def delete(names: List[String]): Unit
  def find(name: String): Player
}

class EntityPlayerService(manager: EntityManager) extends PlayerService {
  override def add(p: Player): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()
    try {
      val player: Player = manager.createNamedQuery("Player.FindPlayerByName")
        .setParameter("name", p.name)
        .getSingleResult.asInstanceOf[Player]
      player.teams = p.teams
      player.battleTag = p.battleTag
    }
    catch {
      case _: NoResultException => manager.persist(p)
    }
    transaction.commit()
  }

  override def delete(names: List[String]): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()

      manager.createNamedQuery("Player.DeleteAllIn")
        .setParameter("ids", names.asJava)
        .executeUpdate
  }

  def findUserByName(name: String): User = manager
    .createNamedQuery("User.FindUserByName")
    .setParameter("username", name)
    .getSingleResult
    .asInstanceOf[User]

  override def find(name: String): Player = ???
}