package controllers

import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request, ResponseHeader, Results}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

// @Singleton annotation: Identifies objects that only instantiate once
@Singleton //@Inject()() annotation: Identifies injectable controllers/objects/methods
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // ControllerComponents: trait describing common dependencies that most controllers rely on

  def index() = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.index()))
  }
  // TODO creates a default page that allows the app to continue to run without needing to implement every page

  def create() = TODO
  def read(id: String) = TODO
  def update(id: String) = TODO
  def delete(id: String) = TODO
}

