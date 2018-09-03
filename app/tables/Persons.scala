package tables

import models.Person
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

class Persons(tag: Tag) extends Table[Person](tag, "persons") {

  def id: Rep[Option[Long]] = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")
  def age: Rep[Int] = column[Int]("age")

  def * : ProvenShape[Person] = (id, name, age) <> ((Person.apply _).tupled, Person.unapply)
}