package models

import play.api.libs.json.{Json, OFormat}

case class Book(
                 id: String,
                 etag: String,
                 volumeInfo: VolumeInfo,
                 saleInfo: SaleInfo
               )

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]
}