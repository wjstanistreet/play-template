package repositories

import models.DataModel
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

  def index(): Future[Either[Int, Seq[DataModel]]]  =
    collection.find().toFuture() map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(NOT_FOUND)
    }

  def create(book: DataModel): Future[Either[Int, result.InsertOneResult]] =
    collection
      .insertOne(book).headOption() map {
      case Some(data) => Right(data)
      case None => Left(NO_CONTENT)
      case _ => Left(NOT_FOUND)
    }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def read(id: String): Future[Either[Int, DataModel]] =
    collection.find(byID(id)).headOption() map {
      case Some(data) => Right(data)
      case None => Left(NO_CONTENT)
      case _ => Left(NOT_FOUND)
    }

  def update(id: String, book: DataModel): Future[Either[Int, result.UpdateResult]] = {
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) //What happens when we set this to false?
    ).headOption() map {
      case Some(data) => Right(data)
      case None => Left(NO_CONTENT)
      case _ => Left(NOT_FOUND)
    }
  }

  def delete(id: String): Future[Either[Int, result.DeleteResult]] =
    collection.deleteOne(
      filter = byID(id)
    ).headOption() map {
      case Some(data) if data.getDeletedCount > 0 => Right(data)
      case Some(data) if data.getDeletedCount == 0 => Left(NO_CONTENT)
      case _ => Left(NOT_FOUND)
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}
