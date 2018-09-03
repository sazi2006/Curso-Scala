package models

import play.api.libs.json.{Json, Reads, Writes}

case class PersonJson(name: String, surname: String)

object PersonJsonList {

  implicit val personWrites: Writes[PersonJson] = Json.writes[PersonJson]
  implicit val personReads: Reads[PersonJson] = Json.reads[PersonJson]

  var list: List[PersonJson]  =  List(
    PersonJson("Alejandro", "Zapata"),
    PersonJson("Lady", "Hincapié"),
    PersonJson("Santiago", "Zapata"),
    PersonJson("Juan José", "Zapata"),
    PersonJson("Marina", "Ibarra"),
  )

  def save(person: PersonJson) = {
    list = list :+ person
  }
}