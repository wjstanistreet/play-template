package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  "ApplicationController .index" should {

    val result = TestApplicationController.index()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create()" should {

  }

  "ApplicationController .read()" should {
//    val result = TestApplicationController.read()(FakeRequest())

//    "return TODO" in {
//      status(result) shouldBe Status.OK
//    }
  }

  "ApplicationController .update()" should {

  }

  "ApplicationController .delete()" should {

  }
}

