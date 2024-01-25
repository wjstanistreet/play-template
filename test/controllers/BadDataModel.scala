package controllers

import models.DataModel
import play.api.libs.json.{Json, OFormat}

// This is a case class meant to represent a class the fails JsValue's validate method
case class BadDataModel(prop: String) {

}

object BadDataModel {
  implicit val formats: OFormat[BadDataModel] = Json.format[BadDataModel]
}
