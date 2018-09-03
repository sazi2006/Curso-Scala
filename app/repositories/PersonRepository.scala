package repositories

import javax.inject.{Inject, Singleton}
import models.Person
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery
import tables.Persons

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PersonRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val persons = TableQuery[Persons]

  import dbConfig._
  import profile.api._

  // todo: handle deletes and updates on nonexistent values

  def create(person: Person): Future[Int] = db.run(
    persons.map(p =>
      (p.name, p.age)) += (person.name, person.age)
  )

  def list(): Future[Seq[Person]] = db.run { persons.result }

  def del(id: Long) : Future[Int] = db.run {
    persons.filter(_.id === id).delete
  }

  def update(person: Person): Future[Int] = {

    val query = for { p <- persons
                      if p.id === person.id } yield p
    db.run(query.update(person))
  }
}