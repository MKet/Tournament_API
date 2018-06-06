package services

import java.io.Closeable

import entities.Tournament
import javax.persistence.EntityManager

trait TournamentService extends Closeable {
  def add(user: Tournament)
  def close(): Unit
}

class EntityTournamentService(manager: EntityManager) extends TournamentService {
  override def add(user: Tournament): Unit = ???

  override def close(): Unit = manager.close()
}
