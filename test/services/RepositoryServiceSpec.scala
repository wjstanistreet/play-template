package services

import baseSpec.BaseSpec
import com.mongodb.client.result.InsertOneResult
import models.{APIError, DataModel}
import org.mongodb.scala.result
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import play.api.libs.json.Json
import play.api.test.Helpers.await
import repositories.DataRepositoryTrait

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Left, Success}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockRepository: DataRepositoryTrait = mock[DataRepositoryTrait]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val repositoryService: RepositoryService = new RepositoryService(mockRepository, executionContext)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "RepositoryService .index()" should {
    "retrieve an empty sequence in an empty database" in {
      (() => mockRepository.index())
        .expects()
        .returning(Future(Right[APIError.BadAPIResponse, Seq[DataModel]](Seq[DataModel]())))
        .once()

      whenReady(repositoryService.index()) { result =>
        result shouldBe Right(Seq[DataModel]())
      }
    }

    "retrieve an non-empty sequence in a non-empty database" in {
      (() => mockRepository.index())
        .expects()
        .returning(Future(Right[APIError.BadAPIResponse, Seq[DataModel]](Seq[DataModel](dataModel))))
        .once()

      whenReady(repositoryService.index()) { result =>
        result shouldBe Right(Seq[DataModel](dataModel))
      }
    }
  }

  "RepositoryService .create()" should {
    val mockedInsertedResult: InsertOneResult = mock[InsertOneResult]

    "create book as a DataModel" in {
      (mockRepository.create(_: DataModel))
        .expects(dataModel)
        .returning(Future(Right[APIError.BadAPIResponse, result.InsertOneResult](mockedInsertedResult)))
        .once()

      whenReady(repositoryService.create(dataModel)) { result =>
        result shouldBe Right(mockedInsertedResult)
      }
    }

    "return no content if unable to create book" in {
      (mockRepository.create(_: DataModel))
        .expects(dataModel)
        .returning(Future(Left[APIError.BadAPIResponse, result.InsertOneResult](APIError.BadAPIResponse(NO_CONTENT, "Unable to create book"))))
        .once()

      whenReady(repositoryService.create(dataModel)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, "Unable to create book"))
      }
    }
  }

  "RepositoryService .read()" should {
    val id: String = "abcd"

    "return a book as a DataModel from the database" in {
      (mockRepository.read(_: String))
        .expects(id)
        .returning(Future(Right(dataModel)))
        .once()

      whenReady(repositoryService.read(id)) { result =>
        result shouldBe Right(dataModel)
      }
    }

    "return no content if there is no book with ID" in {
      (mockRepository.read(_: String))
        .expects(id)
        .returning(Future(Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $id"))))
        .once()

      whenReady(repositoryService.read(id)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $id"))
      }
    }
  }

  "RepositoryService .readByField()" should {
    val fieldName: String = "name"

    "return a book as a DataModel from the database" in {
      (mockRepository.readByField(_: String, _: String))
        .expects(fieldName, "test name")
        .returning(Future(Right(dataModel)))
        .once()

      whenReady(repositoryService.readByField(fieldName, "test name")) { result =>
        result shouldBe Right(dataModel)
      }
    }

    "return no content if there is no book from correct field name" in {
      val nonRealBookName: String = "no books are called this"

      (mockRepository.readByField(_: String, _: String))
        .expects(fieldName, nonRealBookName)
        .returning(Future(Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with field: $fieldName and term: $nonRealBookName"))))
        .once()

      whenReady(repositoryService.readByField(fieldName, nonRealBookName)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with field: name and term: no books are called this"))
      }
    }
  }

  "RepositoryService .update()" should {
    val id: String = "abcd"
    val mockUpdatedResult: UpdateResult = mock[UpdateResult]

    "update a book in the database" in {
      (mockRepository.update(_: String, _: DataModel))
        .expects(id, dataModel)
        .returning(Future(Right[APIError.BadAPIResponse, result.UpdateResult](mockUpdatedResult)))
        .once()

      whenReady(repositoryService.update("abcd", dataModel)) { result =>
        result shouldBe Right(mockUpdatedResult)
      }
    }

    "return no content if ID can't be found" in {
      val nonExistentID: String = "wxyz"

      (mockRepository.update(_: String, _: DataModel))
        .expects(nonExistentID, dataModel)
        .returning(Future(Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $nonExistentID"))))
        .once()

      whenReady(repositoryService.update("wxyz", dataModel)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $nonExistentID"))
      }
    }
  }

  "RepositoryService .updateField()" should {
    val id: String = "abcd"
    val fieldName: String = "name"
    val change: String = "a new name"
    val mockUpdatedResult: UpdateResult = mock[UpdateResult]

    "update a book in the database" in {
      (mockRepository.updateField(_: String, _: String, _: String))
        .expects(id, fieldName, change)
        .returning(Future(Right[APIError.BadAPIResponse, result.UpdateResult](mockUpdatedResult)))
        .once()

      whenReady(repositoryService.updateField(id, fieldName, change)) { result =>
        result shouldBe Right(mockUpdatedResult)
      }
    }

    "return no content if ID can't be found" in {
      val nonExistentID: String = "wxyz"

      (mockRepository.updateField(_: String, _: String, _: String))
        .expects(nonExistentID, fieldName, change)
        .returning(Future(Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to find book with id: $nonExistentID"))))
        .once()

      whenReady(repositoryService.updateField(nonExistentID, fieldName, change)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, "Unable to find book with id: wxyz"))
      }
    }
  }

  "RepositoryService .delete()" should {
    val id: String = "abcd"
    val mockDeletedResult: DeleteResult = mock[DeleteResult]

    "update a book in the database" in {
      (mockRepository.delete(_: String))
        .expects(id)
        .returning(Future(Right[APIError.BadAPIResponse, result.DeleteResult](mockDeletedResult)))
        .once()

      whenReady(repositoryService.delete(id)) { result =>
        result shouldBe Right(mockDeletedResult)
      }
    }

    "return no content if ID can't be found" in {
      val nonExistentID: String = "wxyz"

      (mockRepository.delete(_: String))
        .expects(nonExistentID)
        .returning(Future(Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $nonExistentID"))))
        .once()

      whenReady(repositoryService.delete(nonExistentID)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: wxyz"))
      }
    }
  }
}

