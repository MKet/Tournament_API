package services

import java.io.Closeable

import domain.entities.{Tournament, User}
import javax.persistence.{EntityManager, EntityTransaction}

import scala.collection.JavaConverters._

trait TournamentService extends Closeable {
  def delete(body: List[Int])

  def add(t: Tournament, u: User)

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

    manager.persist(t)

    u.tournament.add(t);
    transaction.commit()
  }

  override def close(): Unit = manager.close()

  override def delete(body: List[Int]): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()

    for (i: Int <- body) {
      manager.createNamedQuery("Tournament.DeleteAllIn")
        .setParameter(":ids", body.asJava)
        .executeUpdate
    }

    transaction.commit()
  }
}
