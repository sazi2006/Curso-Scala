package controllers

import java.nio.file.Paths

import javax.inject.Inject
import models.{PersonJson, PersonJsonList, User}
import models.PersonJsonList._
import org.apache.commons.lang3.Validate
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import scala.concurrent.{ExecutionContext, Future, TimeoutException}

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

  //ActionsComposition

  //1. Crear un ActionsComposition que valide la autenticación de la siguiente forma,
  //si en el request llega un header con el nombre tkn continuará al action
  // de lo contrario retornará un http 403

  def authenticationValidate = Action.async { request =>
    Future {
      request.headers.get("tkn").map(x => Ok(s"The token is $x" )).getOrElse(Forbidden("The token is missing"))

    }
  }

  //ErrorHandling

  //1.  Crear un error handler que implemente de la clase HttpErrorHandler

  //Se creó la clase ErrorHandler en el directorio raíz (app)

  //2. Valide su funcionamiento para errores de servidor generando algún error desde
  // el controlador y evidenciando que retorne lo descrito en el handler

  def handle = Action {
    throw new IllegalStateException("Exception thrown")
    Ok("This wont actually execute")
  }


  //Http Async

  //5. Crear una acción con devolución de futuros

  def asyncRequest = Action.async {

    someCalculation().map(result  => {
      Ok(s"The answer is $result")
    }).recover {
      case e: TimeoutException =>
        InternalServerError(s"Calculation timed out! exception thrown is: ${e.getMessage}")
    }
  }

  def someCalculation(): Future[Int] = {
    Future.successful(42)
  }

  //PlayJson

  //1. Usar Jsvalue

  def jsValue = Action {

    val json: JsValue = Json.obj(
      "name" -> "Alejandro",
      "surname" -> "Zapata",
      "cc" -> "98703347")

    val fieldName: Option[String] = json("name").asOpt[String]
    val fieldSurname: Option[String] = json("surname").asOpt[String]
    val fieldCc: Option[String] = json("cc").asOpt[String]
    Ok(s"The user ${fieldName.get} ${fieldSurname.get} has the identification number ${fieldCc.get}")
  }

  //2. Usar writes

  def jsWrite = Action {

    implicit val userWrite: Writes[User] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "surname").write[String] and
        (JsPath \ "cc").write[String]
      ) (unlift(User.unapply))

    val user = User("Alejandro", "Zapata", "98703347")
    Ok(Json.toJson(user))
  }

  def jsAutomaticWrite = Action {

    implicit val userWrite: Writes[User] = Json.writes[User]
    val user = User("Alejandro", "Zapata", "98703347")

    Ok(Json.toJson(user))
  }

  //3. Aprender a buscar valores en Json

  def jsSearch = Action {

    val json: JsValue = Json.obj(
      "name" -> "Alejandro",
      "surname" -> "Zapata",
      "info" -> Json.obj(
        "login" -> "alejandro.zapata",
        "cc" -> "98703347"
      )
    )

    val login = (json \ "info" \ "login").asOpt[String]
    val cc = (json \ "info" \ "cc").asOpt[String]

    Ok(s"Your login is: ${login.get} and your identification number is: ${cc.get}")
  }

  //4. Usar validate

  def jsValidate = Action {

    implicit val userReads: Reads[User] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "surname").read[String] and
        (JsPath \ "cc").read[String](minLength[String](6))
      ) (User.apply _)

    val json: JsValue = Json.obj(
      "name" -> "Alejandro",
      "surname" -> "Zapata",
      "cc" -> "98703347")

    val jsonResult = json.validate[User]

    jsonResult match {
      case JsSuccess(user: User, path: JsPath) => Ok(s"The user is: ${user.name} ${user.surname} with identification number: ${user.cc}")
      case e: JsError => InternalServerError("Errors: " + JsError.toJson(e).toString())
    }
  }

  //JsonHttp

  //1. Definir un objeto de dominio persona
  //Se creó la clase Person en models

  //2. Ser capaz de devolver una lista de personas (con y sin bloqueo "futures")

  def listSyncPersons = Action {
    Ok(Json.toJson(PersonJsonList.list))  }

  def listAsyncPersons = Action.async {
    Future.successful(
      Ok(Json.toJson(PersonJsonList.list))
    )
  }

  //3. Ser capaz de recibir una persona para crearla (con y sin bloqueo "futures")

  def addSync = Action(parser.json) { request => {
      val person: JsResult[PersonJson] = request.body.validate[PersonJson]
      person match {
        case JsSuccess(person: PersonJson, path: JsPath) => {
          PersonJsonList.save(person)
          Ok(Json.toJson(PersonJsonList.list))
        }
        case e: JsError => BadRequest(JsError.toJson(e).toString())
      }
    }
  }

  def addASync = Action.async(parser.json) { request => {
      val person: JsResult[PersonJson] = request.body.validate[PersonJson]
      Future {
        person match {
          case JsSuccess(person: PersonJson, path: JsPath) => {
            PersonJsonList.save(person)
            Ok(Json.toJson(PersonJsonList.list))
          }
          case e: JsError => BadRequest(JsError.toJson(e).toString())
        }
      }
    }
  }

  //Cargar y retornar archivos

  //1. Realizar una prueba de concepto cargando un archivo desde un index.html,
  // dicho archivo se debera almacenar en una carpeta del disco c

  def fileUpload = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>

      // only get the last part of the filename
      // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
      val filename = Paths.get(file.filename).getFileName

      file.ref.moveTo(Paths.get(s"D:/Proyectos/Scala/play-scala-starter-example/public/upload/$filename"), replace = true)
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.HomeController.indexForm).flashing(
        "error" -> "Missing file")
    }
  }

  //2. Habilitar la limpieza de archivos temporales
  //Se habilitó el reaper en el archivo application.conf




}
