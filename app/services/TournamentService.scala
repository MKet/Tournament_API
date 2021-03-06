package services

import java.io.Closeable
import java.util

import domain.entities.{Tournament, User}
import javax.persistence.{EntityManager, EntityTransaction}

import scala.collection.JavaConverters._

trait TournamentService extends Closeable {
  def getAllOwnedBy(user: String): List[Tournament]

  def add(t: Tournament, u: User): Unit

  def delete(body: List[Int], user: String): Unit

  def close(): Unit
}

class EntityTournamentService(manager: EntityManager) extends TournamentService {
  override def add(t: Tournament, u: User): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()

    for (team <- t.teams.asScala) {
      manager.persist(team)
    }

    for (m <- t.matches.asScala) {
      manager.persist(m)
    }
    t.owner = u

    manager.persist(t)
    transaction.commit()
  }

  override def close(): Unit = if (manager.isOpen) manager.close()

  override def delete(body: List[Int], user: String): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()

    manager.createNamedQuery("Tournament.DeleteAllIn")
      .setParameter("ids", body.asJava)
      .setParameter("owner", user)
      .executeUpdate

    transaction.commit()
  }

  override def getAllOwnedBy(user: String): List[Tournament] =
    manager.createNamedQuery("Tournament.GetAllOwnerBy")
      .setParameter("owner", user)
      .getResultList.asInstanceOf[util.List[Tournament]].asScala.toList
}
