package repositories
import models.messages.InterchangeControlReference
import org.scalatest.{BeforeAndAfterEach, FreeSpec, MustMatchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import reactivemongo.play.json.ImplicitBSONHandlers._
import reactivemongo.play.json.collection.JSONCollection
import services.DateTimeService
import services.mocks.MockDateTimeService

import scala.concurrent.ExecutionContext.Implicits.global

class InterchangeControlReferenceIdRepositorySpec
  extends FreeSpec
    with MustMatchers
    with MongoSuite
    with ScalaFutures
    with BeforeAndAfterEach
    with IntegrationPatience
    with MockDateTimeService {

  private val appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder()
    .overrides(
      bind[DateTimeService].toInstance(mockTimeService)
    )

  override def beforeEach(): Unit = {
    super.beforeEach()
    database.flatMap(_.drop()).futureValue
  }

  "InterchangeControlReferenceIdRepository" - {

    "must generate correct InterchangeControlReference when no record exists within the database" in {

      val app = appBuilder.build()

      running(app) {
        mockDateFormatted("20190101")

        val service: InterchangeControlReferenceIdRepository = app.injector.instanceOf[InterchangeControlReferenceIdRepository]

        val first = service.nextInterchangeControlReferenceId().futureValue

        first mustBe InterchangeControlReference("20190101", 1)

        val second = service.nextInterchangeControlReferenceId().futureValue

        second mustBe InterchangeControlReference("20190101", 2)
      }
    }

    "must generate correct InterchangeControlReference when the collection already has a document in the database" in {

      mockDateFormatted("20190101")

      database.flatMap {
        db =>
          db.collection[JSONCollection]("interchange-control-reference-ids")
            .insert(ordered = false)
            .one(
              Json.obj(
                "_id"        -> mockTimeService.dateFormatted,
                "last-index" -> 1
              ))
      }.futureValue

      val app = appBuilder.build()

      running(app) {
        val service: InterchangeControlReferenceIdRepository = app.injector.instanceOf[InterchangeControlReferenceIdRepository]

        val first = service.nextInterchangeControlReferenceId().futureValue
        val second = service.nextInterchangeControlReferenceId().futureValue

        first.index mustEqual 2
        second.index mustEqual 3
      }
    }

  }

}
