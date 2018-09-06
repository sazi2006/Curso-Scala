package controllers

import javax.inject.Inject
import models.Person
import models.Person.personFormat
import play.api.cache._
import play.api.libs.json.Json
import play.api.mvc._
import repositories.PersonRepositoryImp
import services.PersonService
import utils.Validate

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject() (personRepo: PersonRepositoryImp, personService: PersonService, parser: PlayBodyParsers,
                                 cc: MessagesControllerComponents, cache: AsyncCacheApi, cached: Cached, parserValidation: Validate)
                                (implicit  ec: ExecutionContext) extends AbstractController(cc) {

  def list = Action.async {

    personService.listPersons().map { persons =>
      Ok(Json.toJson(persons))
    }
  }

  def create = Action.async(parserValidation.validateJson[Person]) {

    request => {
      personRepo.create(request.body).map(code => {
        Ok(s"Saved person")
      })
    }
  }

  def delete(id: Long) = Action.async {

    personRepo.del(id).map(code => {
      Ok(s"Person deleted")
    })
  }

  def update() = Action.async(parserValidation.validateJson[Person]) {

    request => {
      personRepo.update(request.body).map(code => {
        Ok("The person has been updated")
      })
    }
  }

  // Play cache
  //1. Habilitar el uso de cache

  //2. Guardar y recuperar datos de cache

  //3. Sobre el ejemplo de base de datos, guardar el listado de personas en cache durante 15 minutos,
  // en caso de no existir valores en cache se deberá consultar la información de la base de datos

  def listCached = Action.async {

    val persons: Future[Seq[Person]] = cache.getOrElseUpdate[Seq[Person]]("persons", Duration(15, MINUTES)) {
      println("Information loaded from the cache")
      personRepo.list()
    }

    persons
      .map(persons => {
        Ok(Json.toJson(persons))
      }).recover {
      case _ => InternalServerError("this wont execute")
    }
  }

  def listHttpCached = cached("persons-http").default(1) {

    Action.async {
      println("Information loaded from the http cache")
      personRepo.list().map { persons =>
        Ok(Json.toJson(persons))
      }
    }
  }

}


