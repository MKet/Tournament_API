package services

import java.io.Closeable

import domain.entities.Player
import javax.persistence.{EntityManager, EntityTransaction, NoResultException}

import scala.collection.JavaConverters._

trait PlayerService extends Closeable{
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

  override def find(name: String): Player = manager
    .createNamedQuery("Player.FindPlayerByName")
    .setParameter("name", name)
    .getSingleResult
    .asInstanceOf[Player]

  override def close(): Unit = if (manager.isOpen) manager.close()
}