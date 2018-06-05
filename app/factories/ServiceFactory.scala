package factories

import javax.inject.Singleton
import javax.persistence.Persistence
import services.{EntityUserService, UserService}

trait ServiceFactory {
  def getUserService : UserService
}

@Singleton
class EntityServiceFactory extends ServiceFactory {

  private val defaultManager: String = "manager1"

  def getUserService: UserService = new EntityUserService(
    Persistence.createEntityManagerFactory(defaultManager).createEntityManager()
  )
}
