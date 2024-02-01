package models

import play.api.libs.json.{Json, OFormat}

case class Shelf(
                  kind: String,
                  totalItems: Int,
                  items: Seq[Book])

object Shelf {
  implicit val formats: OFormat[Shelf] = Json.format[Shelf]
}
