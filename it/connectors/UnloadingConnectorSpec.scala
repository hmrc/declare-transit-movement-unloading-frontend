package connectors

import models.Movement
import org.scalatest.{FreeSpec, MustMatchers}
import uk.gov.hmrc.http.HeaderCarrier
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.ExecutionContext.Implicits.global

class UnloadingConnectorSpec extends FreeSpec with ScalaFutures with IntegrationPatience with WireMockSuite with MustMatchers {

  override protected def portConfigKey: String = "microservice.services.arrivalsBackend.port"

  private lazy val connector = app.injector.instanceOf[UnloadingConnector]

  implicit val hc = HeaderCarrier()

  "UnloadingConnectorSpec" - {

    "GET" - {

      "should retrieve unloading permission" in {

        server.stubFor(
          get("/common-transit-convention-trader-at-destination/messages")
            .willReturn(
              aResponse()
                .withStatus(200)
            )
        )

        connector.get() mustBe Some(Movement("test"))
      }
    }
  }

}
