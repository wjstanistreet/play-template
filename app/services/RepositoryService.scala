package services

import cats.data.EitherT
import models.{APIError, DataModel}
import play.api.libs.json.{JsValue, Json}
import repositories.DataRepositoryTrait
import org.mongodb.scala.result
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, NO_CONTENT}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(
                                   dataRepository: DataRepositoryTrait,
                                   implicit val ec: ExecutionContext
                                 ) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = {
    dataRepository.index() map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, result.InsertOneResult]] = {
    dataRepository.create(book) map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.read(id).map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def readByField(fieldName: String, term: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    if (!DataModel.fields.contains(fieldName)) return Future(Left(APIError.BadAPIResponse(BAD_REQUEST, s"Field, $fieldName, not contained in DataModel")))

    dataRepository.readByField(fieldName, term) map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    dataRepository.update(id, book) map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def updateField(id: String, fieldName: String, change: String): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    if (!DataModel.fields.contains(fieldName))  return Future.successful(Left(APIError.BadAPIResponse(BAD_REQUEST, s"Field, $fieldName, not contained in object $id")))
    if (fieldName.equals("_id"))                return Future.successful(Left(APIError.BadAPIResponse(BAD_REQUEST, "You can't update the object's ID")))

    dataRepository.updateField(id, fieldName, change) map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] = {
    dataRepository.delete(id) map {
      case Right(data) => Right(data)
      case Left(error) => Left(error)
    }
  }
}
