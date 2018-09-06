package utils

import javax.inject.Inject
import models.{Person, PersonJson}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext

class Validate @Inject()(parser: PlayBodyParsers)
                        (implicit exec: ExecutionContext) {

  implicit val personJsonWrites: Writes[PersonJson] = Json.writes[PersonJson]

  implicit val personJsonReads: Reads[PersonJson] = Json.reads[PersonJson]

  implicit val personReads: Reads[Person] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "age").read[Int]
    )(Person.apply _)

  def validateJson[A : Reads] = parser.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )
}
