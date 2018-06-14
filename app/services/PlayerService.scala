package services

import domain.entities.{Player, Team}
import javax.persistence.EntityManager

trait PlayerService {
  def add(p: Player): Unit
  def delete(name: String): Unit
  def find(name: String): Player
}

class EntityPlayerService(manager: EntityManager) extends PlayerService {
  override def add(p: Player): Unit = ???
  override def delete(name: String): Unit = ???
  override def find(name: String): Player = ???
}