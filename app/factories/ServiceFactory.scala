package factories

import javax.inject.Singleton
import javax.persistence.Persistence
import services.{EntityUserService, UserService}

trait ServiceFactory {
  def getUserService : UserService
}

@Singleton
class EntityServiceFactory extends ServiceFactory {

  private val EntityManager = Persistence.createEntityManagerFactory("manager1").createEntityManager()

  def getUserService: UserService = new  EntityUserService(EntityManager)
}
