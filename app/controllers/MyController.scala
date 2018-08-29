package controllers

import javax.inject._
import play.api.Configuration
import play.api.mvc.{AbstractController, ControllerComponents}

class MyController @Inject() (config: Configuration, c: ControllerComponents) extends AbstractController(c) {
  def getName = Action {
    Ok(config.get[String]("application.name"))
  }
}