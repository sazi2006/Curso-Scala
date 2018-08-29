package controllers

import javax.inject.Inject
import org.apache.commons.lang3.Validate
import play.api.mvc.{AbstractController, ControllerComponents, Cookie, PlayBodyParsers}

import scala.concurrent.ExecutionContext

class ChallengeController @Inject()(cc: ControllerComponents, parser: PlayBodyParsers, parserValidation: Validate)
                                   (implicit exec: ExecutionContext) extends AbstractController(cc) {

  // Actions, Controllers y Results

  //2. Crear diversar acciones y mapearlas en el archivo routes, que retornen mensajes con codigos http 200, 401y 500
  def ok = Action { request => Ok("200 OK") }
  def unauthorized = Action { request => Unauthorized("401 UNAUTHORIZED") }
  def internalServerError = Action { request => InternalServerError("500 INTERNAL_SERVER_ERROR") }

  //3. Crear un action que devuelva un archivo .pdf de su elección
  def scalaBook = Action {
    Ok.sendFile(
      new java.io.File(
        "D:\\Proyectos\\Scala\\play-scala-starter-example\\public\\pdf\\scala-impatient.pdf"),
      false)
  }

  // Routing

  //1. En el archivo routes definir rutas del tipo GET, POST, PUT, DELETE, PATCH e implementarlas en controladores
  def getRequest = Action { request => Ok("Get request!") }
  def postRequest = Action { request => Ok("Post request!") }
  def putRequest = Action { request => Ok("Put request!") }
  def deleteRequest = Action { request => Ok("Delete request!") }
  def patchRequest = Action { request => Ok("Patch request!") }

  //2. Usar query params, Path params
  def sendParameter(parameter: String) = Action { request =>
    Ok(s"Hello, the parameter: $parameter has been received!")
  }

  //3. Recibir e imprimir por consola headers (Puedes apoyarte de jmeter o postman)
  def requestWithHeader() = Action {
    request => {
      val headers = request.headers;
      val login = headers.get("Login").get;
      Ok(s"Request received for login: $login")
    }
  }

  //4. Recibir e imprimir por consola cookies (Puedes apoyarte de jmeter o postman)
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

  //Manipulando Results

  //1. Cambiar sobre algún action, el Content-Type
  def customContentType() = Action { request =>
    Ok(<h1>Hello, this Content-Type is HTML!</h1>).as(HTML)
  }

  //2. Adicionar algún header en el response
  def customHeader() = Action { request =>
    Ok("Hello, these Headers were added!").withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "Header added")
  }

  //3. Adicionar una cookie en el reponse
  def addCookie() = Action { request =>
    Ok("Hello, the owner Cookie was added")
      .withCookies(Cookie("owner", "Alejandro"))
      .bakeCookies()
  }




}
