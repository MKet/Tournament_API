package services

import java.io.Closeable

import domain.entities.{Tournament, User}
import javax.persistence.{EntityManager, EntityTransaction}

trait TournamentService extends Closeable {
  def add(t: Tournament)
  def close(): Unit
}

class EntityTournamentService(manager: EntityManager) extends TournamentService {
  override def add(t: Tournament): Unit = {
    val transaction: EntityTransaction = manager.getTransaction

    transaction.begin()
    manager.persist(t)
    transaction.commit()
  }

  override def close(): Unit = manager.close()
}
