package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.APIError.BadAPIResponse
import models.{APIError, Book, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockConnector: LibraryConnector              = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext  = app.injector.instanceOf[ExecutionContext]
  val testService: LibraryService                  = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "numSales" -> 100
  )

  "getGoogleDataModel" should {
    val url: String = "testUrl"

    "return a book as a DataModel" in {
      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.right[APIError](Future(gameOfThrones.as[DataModel])))
        .once()

      whenReady(testService.getGoogleDataModel(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(gameOfThrones.as[DataModel])
      }
    }

    "return an error" in {
      val url: String = "testUrl"

      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.left[DataModel](Future(APIError.BadAPIResponse(404, "Book not found"))))
        .once()

      whenReady(testService.getGoogleDataModel(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(APIError.BadAPIResponse(404, "Book not found"))
      }
    }
  }
}
