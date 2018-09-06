package services

import javax.inject.Inject
import models.Person
import repositories.{PersonRepository}

import scala.concurrent.Future

class PersonService @Inject() (personRepo: PersonRepository) {

  def listPersons(): Future[Seq[Person]] = {

    // this is just a wrapper. and for testing purposes
    // we won't code any logic here
    personRepo.list()
  }

  def savePerson(person: Person): Future[Int]  = {
    personRepo.create(person)
  }
}