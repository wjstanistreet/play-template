package models

import play.api.libs.json.{Json, OFormat}

case class Book(
                 kind: String,
                 id: String,
                 etag: String,
                 selfLink: String)

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]
}