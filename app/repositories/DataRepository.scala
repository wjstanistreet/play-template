package repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.{SingleObservable, result}
import play.api.http.Status._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  =
    collection.find().toFuture() map {
      case books: Seq[DataModel]  => Right(books)
      case _                      => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, result.InsertOneResult]] =
    collection.insertOne(book).headOption() map {
      case Some(data) => Right(data)
      case None       => Left(APIError.BadAPIResponse(NO_CONTENT, "Unable to create book"))
      case _          => Left(APIError.BadAPIResponse(NOT_FOUND, "Error creating book"))
    }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption() map {
      case Some(data) => Right(data)
      case None       => Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $id"))
      case _          => Left(APIError.BadAPIResponse(NOT_FOUND, "Error reading book"))
    }

  private def byField(fieldName: String, term: String): Bson =
    Filters.and(
      Filters.equal(fieldName, term)
    )

  def readByField(fieldName: String, term: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byField(fieldName, term)).headOption() map {
      case Some(data) => Right(data)
      case None       => Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with field: $fieldName and term: $term"))
      case _          => Left(APIError.BadAPIResponse(NOT_FOUND, "Error reading book"))
    }

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, result.UpdateResult]] = {
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) //What happens when we set this to false?
    ).headOption() map {
      case Some(data) => Right(data)
      case None       => Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $id"))
      case _          => Left(APIError.BadAPIResponse(NOT_FOUND, "Error updating book"))
    }
  }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, result.DeleteResult]] =
    collection.deleteOne(
      filter = byID(id)
    ).headOption() map {
      case Some(data) if data.getDeletedCount > 0   => Right(data)
      case Some(data) if data.getDeletedCount == 0  => Left(APIError.BadAPIResponse(NO_CONTENT, s"Unable to retrieve book with id: $id"))
      case _ => Left(APIError.BadAPIResponse(NOT_FOUND, "Error deleting book"))
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}
