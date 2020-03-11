package connectors

import models.Movement
import org.scalatest.{FreeSpec, MustMatchers}
import uk.gov.hmrc.http.HeaderCarrier
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.ExecutionContext.Implicits.global

class UnloadingConnectorSpec extends FreeSpec with ScalaFutures with
  IntegrationPatience with WireMockSuite with MustMatchers  with ScalaCheckPropertyChecks {

  override protected def portConfigKey: String = "microservice.services.arrivalsBackend.port"

  private def connector: UnloadingConnector = app.injector.instanceOf[UnloadingConnector]

  implicit val hc = HeaderCarrier()

  private val unloadingJson: String =
                                 """
                                   |[
                                   |{
                                   |"messages" :  "test"
                                   |}
                                   |]
                                   |""".stripMargin

  "UnloadingConnectorSpec" - {

    "GET" - {

      "should retrieve unloading permission" in {

        server.stubFor(
          get("/common-transit-convention-trader-at-destination/messages")
            .willReturn(okJson(unloadingJson)
            ))

        connector.get.futureValue mustBe Some(Movement("test"))
      }
    }
  }

}
