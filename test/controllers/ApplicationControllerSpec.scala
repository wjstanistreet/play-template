package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.http.HttpEntity.Strict
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
    service,
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

  private val differentIDModel: DataModel = DataModel(
    "wxyz",
    "new test name",
    "new test description",
    300
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

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)
      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }

    "return a bad request if an incorrect validated model is an argument" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(badDataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {
      beforeEach()
      createBook()

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)
      afterEach()
    }

    "return no content if a book is not in database" in {
      val readResult: Future[Result] = TestApplicationController.read("wxyz")(FakeRequest())

      status(readResult) shouldBe Status.NO_CONTENT
    }
  }

  "ApplicationController .update()" should {

    "edit an existing book in the database" in {
      beforeEach()
      createBook()

      val updateRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updatedDataModel))
      val updatedResult: Future[Result] = TestApplicationController.update("abcd")(updateRequest)

      status(updatedResult) shouldBe Status.ACCEPTED
      contentAsJson(updatedResult).as[JsValue] shouldBe Json.toJson(updatedDataModel)
      afterEach()
    }

    "return a bad request if an incorrect validated model is an argument" in {
      beforeEach()
      createBook()

      val updateRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(badDataModel))
      val updatedResult: Future[Result] = TestApplicationController.update("abcd")(updateRequest)

      status(updatedResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }

    "upsert an entry if ID not found" in {
      beforeEach()

      val updateRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(differentIDModel))
      val updatedResult: Result = await(TestApplicationController.update("wxyz")(updateRequest))

      val readResult: Future[Result] = TestApplicationController.read("wxyz")(FakeRequest())

      updatedResult.header.status shouldBe Status.ACCEPTED
      contentAsJson(readResult) shouldBe Json.toJson(differentIDModel)
      afterEach()
    }
  }

  "ApplicationController .delete()" should {

    "remove an entry in the database" in {
      beforeEach()
      createBook()

      val readPreDeletion: Result = await(TestApplicationController.read("abcd")(FakeRequest()))
      val deleteResult: Future[Result] = TestApplicationController.delete("abcd")(FakeRequest())
      val readPostDeletion: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      readPreDeletion.header.status shouldBe Status.OK
      status(deleteResult) shouldBe Status.ACCEPTED
      status(readPostDeletion) shouldBe Status.NO_CONTENT
      afterEach()
    }

    "return no content if there is no book with an ID" in {
      beforeEach()
      val deleteResult: Future[Result] = TestApplicationController.delete("wxyz")(FakeRequest())

      status(deleteResult) shouldBe Status.NO_CONTENT
      afterEach()
    }
  }

  "ApplicationController .getGoogleBook" should {

    "test" in {
      val testVal: Result = await(TestApplicationController.getGoogleBook("flowers+inauthor", "O9ZCAQAAMAAJ")(FakeRequest()))

      testVal shouldBe 1
    }

  }

  override def beforeEach(): Unit = await(repository.deleteAll())

  def createBook(): Result = {
    val createRequest: FakeRequest[JsValue] = buildPost("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
    await(TestApplicationController.create()(createRequest))
  }

  override def afterEach(): Unit = await(repository.deleteAll())
}

