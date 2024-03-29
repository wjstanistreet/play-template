package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, ResponseHeader, Results}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// @Singleton annotation: Identifies objects that only instantiate once
@Singleton //@Inject()() annotation: Identifies injectable controllers/objects/methods
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository,
                                       implicit val ec: ExecutionContext
                                     ) extends BaseController {
  // ControllerComponents: trait describing common dependencies that most controllers rely on

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok(Json.toJson(item))
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }
  // TODO creates a default page that allows the app to continue to run without needing to implement every page

  // Returns an Action[JsValue] -> Json to be received by client
  // Action.async(parse.json) -> .async creates Future; parse.json will parse the request
  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    // request.body.validate[DataModel] -> validates the request body (in json)
    // based on the implicit format in the DataModel object; returns a JsResult
    request.body.validate[DataModel] match {
      // if it's successful, call the repository service layer
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map {
          case Right(_) => Created
          case Left(error) => Status(error)
        }
      case JsError(_) => Future(BadRequest) // if the validation is unsuccessful, send a bad request to the client
    }
  }
  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    // retrieves the data by calling .read and maps the Future to a Result, in this case Ok{json data}
    dataRepository.read(id).map {
      case Right(data) => Ok(Json.toJson(data))
      case Left(error) => Status(error)
    }
  }

  // similar to .create(): parse request, validate json body, then call repository service
  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).map {
          case Right(_) => Accepted(Json.toJson(dataModel))
          case Left(_) => NotFound}
      case JsError(_) => Future(BadRequest)
    }
  }
  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id) map {
      case Right(_) => Accepted
      case Left(error) => Status(error)
    }
  }
}

