package controllers

import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

// @Singleton annotation: Identifies objects that only instantiate once
@Singleton //@Inject()() annotation: Identifies injectable controllers/objects/methods
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  // ControllerComponents: trait describing common dependencies that most controllers rely on

  def index() = TODO
  // TODO creates a default page that allows the app to continue to run without needing to implement every page

  def createMatching() = TODO
  def readMatching(id: String) = TODO
  def updateMatching(id: String) = TODO
  def deleteMatching(id: String) = TODO
}

