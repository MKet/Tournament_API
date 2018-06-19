package factories

import java.io.Closeable

import javax.persistence.Persistence
import services._

trait ServiceFactory extends Closeable {
  def UserService: UserService

  def TournamentService: TournamentService

  def PlayerService: PlayerService
}

class EntityServiceFactory extends ServiceFactory {

  private val defaultManager: String = "manager1"
  private val manager = Persistence.createEntityManagerFactory(defaultManager).createEntityManager()

  override def UserService: UserService = new EntityUserService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )

  override def TournamentService: TournamentService = new EntityTournamentService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )

  override def PlayerService: PlayerService = new EntityPlayerService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )

  override def close(): Unit = if (manager.isOpen) manager.close()

}
