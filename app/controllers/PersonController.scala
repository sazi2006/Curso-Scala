package controllers

import repositories.PersonRepository
import models.Person.personFormat
import javax.inject.Inject
import models.Person
import play.api.cache._
import play.api.libs.json.Json
import play.api.mvc._
import utils.Validate

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject() (personRepo: PersonRepository, parser: PlayBodyParsers, cc: MessagesControllerComponents,
                                 cache: AsyncCacheApi, cached: Cached, parserValidation: Validate)
                                (implicit  ec: ExecutionContext) extends AbstractController(cc) {

  def list = Action.async {

    personRepo.list().map { persons =>
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

  // play cache
  def listCached = Action.async {

    val persons: Future[Seq[Person]] = cache.getOrElseUpdate[Seq[Person]]("persons", Duration(15, MINUTES)) {
      println("take notes, this wont print a second time (for 15 minutes, that is)!")
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
      println("take notes, this wont print a second time !")
      personRepo.list().map { persons =>
        Ok(Json.toJson(persons))
      }
    }
  }

  
}