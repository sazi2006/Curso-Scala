package controllers

import javax.inject.Inject
import org.apache.commons.lang3.Validate
import play.api.mvc.{AbstractController, ControllerComponents, PlayBodyParsers}

import scala.concurrent.ExecutionContext

class ChallengeController @Inject()(cc: ControllerComponents, parser: PlayBodyParsers, parserValidation: Validate)
                                   (implicit exec: ExecutionContext) extends AbstractController(cc) {

  // Actions, Controllers y Results
  def ok = Action { request => Ok("200 OK") }
  def unauthorized = Action { request => Unauthorized("401 UNAUTHORIZED") }
  def internalServerError = Action { request => InternalServerError("500 INTERNAL_SERVER_ERROR") }

  def scalaBook = Action {
    Ok.sendFile(
      new java.io.File(
        "D:\\Proyectos\\Scala\\play-scala-starter-example\\public\\pdf\\scala-impatient.pdf"),
      false)
  }

  // Routing
  def getRequest = Action { request => Ok("Get request!") }
  def postRequest = Action { request => Ok("Post request!") }
  def putRequest = Action { request => Ok("Put request!") }
  def deleteRequest = Action { request => Ok("Delete request!") }
  def patchRequest = Action { request => Ok("Patch request!") }



}
