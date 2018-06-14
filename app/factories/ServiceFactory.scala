package factories

import java.io.Closeable

import javax.persistence.Persistence
import services.{EntityTournamentService, EntityUserService, TournamentService, UserService}

trait ServiceFactory extends Closeable {
  def getUserService: UserService

  def getTournamentService: TournamentService
}

class EntityServiceFactory extends ServiceFactory {

  private val defaultManager: String = "manager1"
  private val manager = Persistence.createEntityManagerFactory(defaultManager).createEntityManager()

  override def getUserService: UserService = new EntityUserService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )

  override def getTournamentService: TournamentService = new EntityTournamentService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )

  override def close(): Unit = manager.close()
}
