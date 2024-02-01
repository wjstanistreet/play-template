package models

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(
                     title: String,
                     publishedDate: String,
                     pageCount: Int,
                     printType: String,
                     maturityRating: String,
                     imageLinks: ImageLinks
                     )

object VolumeInfo {
  implicit val formats: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}