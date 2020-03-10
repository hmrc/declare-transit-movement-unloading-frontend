package connectors

import config.FrontendAppConfig
import models.Movement
import org.scalatest.{FreeSpec, MustMatchers}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class UnloadingConnectorSpec extends FreeSpec with MustMatchers with WireMockSuite {

  private val config = app.injector.instanceOf[FrontendAppConfig]

  private val connector = app.injector.instanceOf[UnloadingConnector]

  implicit val hc = HeaderCarrier()

  "UnloadingConnectorSpec" - {


    "GET" - {

      "should retrieve unloading permission" in {

        connector.get() mustBe Some(Movement("test"))
      }
    }
  }

  override protected def portConfigKey: String = "microservices.services.auth.port"
}
