package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  private val updatedDataModel: DataModel = DataModel(
    "abcd",
    "edited test name",
    "edited test description",
    200
  )

  private val badDataModel: BadDataModel = BadDataModel("test")

  "ApplicationController .index" should {
    val result = TestApplicationController.index()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }

    "fail with a BadRequest" in {

    }

    "fail if the wrong class is validated" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(badDataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }

  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(createdResult) shouldBe Status.CREATED
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)
    }
  }

  "ApplicationController .update()" should {

    "edit an existing book in the database" in {
      beforeEach()
      val createdResult: Future[Result] = createBook()

      val updateRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updatedDataModel))
      val updatedResult: Future[Result] = TestApplicationController.update("abcd")(updateRequest)

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(createdResult) shouldBe Status.CREATED
      status(updatedResult) shouldBe Status.ACCEPTED
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(updatedDataModel)
      afterEach()
    }
  }

  "ApplicationController .delete()" should {

    "remove an entry in the database" in {
      beforeEach()
      val createdResult: Future[Result] = createBook()

      val deleteResult: Future[Result] = TestApplicationController.delete("abcd")(FakeRequest())

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(createdResult) shouldBe Status.CREATED
      status(deleteResult) shouldBe Status.ACCEPTED
      status(readResult) shouldBe Status.NOT_FOUND
      afterEach()
    }
  }

  override def beforeEach(): Unit = repository.deleteAll()

  def createBook(): Future[Result] = {
    val createRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
    TestApplicationController.create()(createRequest)
  }

  override def afterEach(): Unit = repository.deleteAll()
}

