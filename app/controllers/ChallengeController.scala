package controllers

import javax.inject.Inject
import org.apache.commons.lang3.Validate
import play.api.mvc.{AbstractController, ControllerComponents, Cookie, PlayBodyParsers}

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

  def sendParameter(parameter: String) = Action { request =>
    Ok(s"Hello, the parameter: $parameter has been received!")
  }

  def requestWithHeader() = Action {
    request => {
      val headers = request.headers;
      val login = headers.get("Login").get;
      Ok(s"Request received for login: $login")
    }
  }

  def requestWithCookies() = Action { request =>
    val someOrg = request.cookies.get("org")
    val org = someOrg match {
      case Some(n) => n
      case None => "Without cookies"
    }
    /*Si quiero mostrar solo el value de la cookie
      val orgValue = org.toString.split(",")
      Ok(s"Received the cookie: ${orgValue(1)}")*/
      Ok(s"Received the cookie: $org")
      .withCookies(Cookie("org", "Ceiba"))
      .bakeCookies()
  }



}
