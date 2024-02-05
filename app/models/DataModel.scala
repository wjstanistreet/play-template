package models

import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String, name: String, description: String, numSales: Int) {
}

object DataModel {
  val fields: Set[String] = Set("_id", "name", "description", "numSales")
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]
}