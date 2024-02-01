package models

import play.api.libs.json.{Json, OFormat}

case class SaleInfo(country: String)

object SaleInfo {
  implicit val formats: OFormat[SaleInfo] = Json.format[SaleInfo]
}