package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.Movement
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class UnloadingConnectorSpec extends FreeSpec with ScalaFutures with
  IntegrationPatience with WireMockSuite with MustMatchers  with ScalaCheckPropertyChecks {

  override protected def portConfigKey: String = "microservice.services.arrivals-backend.port"

  private def connector: UnloadingConnector = app.injector.instanceOf[UnloadingConnector]

  implicit val hc = HeaderCarrier()

  private val unloadingJson: String =
                                 """
                                   |{
                                   |"messages" :  "test"
                                   |}
                                   """.stripMargin

  private val uri = "/common-transit-convention-trader-at-destination/messages"

  "UnloadingConnectorSpec" - {

    "GET" - {

      "should retrieve unloading permission" in {

        server.stubFor(
          get(uri)
            .willReturn(okJson(unloadingJson)
            ))

        connector.get().futureValue mustBe Some(Movement("test"))
      }

      "should handle a 404 response" in {

        server.stubFor(
          get(uri)
            .willReturn(notFound)
        )
        connector.get().futureValue mustBe None
      }

      "should handle an exception" in {

        server.stubFor(
          get(uri)
            .willReturn(serverError)
        )
        connector.get().futureValue mustBe None
      }
    }
  }

}
