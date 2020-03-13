package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.{Movement, MovementReferenceNumber}
import org.scalacheck.Gen
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsArray, JsObject, JsString}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class UnloadingConnectorSpec extends FreeSpec with ScalaFutures with
  IntegrationPatience with WireMockSuite with MustMatchers  with ScalaCheckPropertyChecks {

  import UnloadingConnectorSpec._

  override protected def portConfigKey: String = "microservice.services.arrivals-backend.port"

  private def connector: UnloadingConnector = app.injector.instanceOf[UnloadingConnector]

  implicit val hc = HeaderCarrier()

  "UnloadingConnectorSpec" - {

    "GET" - {

      "should handle a 200 response" in {
        server.stubFor(
          get(uri)
            .willReturn(okJson(unloadingJson)
            ))

        connector.get(mrn).futureValue mustBe Some(Seq(Movement("test")))
      }

      "should handle a 404 response" in {

        server.stubFor(
          get(uri)
            .willReturn(notFound)
        )
        connector.get(mrn).futureValue mustBe None
      }

      "should handle client and server errors" in {
        forAll(responseCodes) {
          code =>
            server.stubFor(
              get(uri)
                .willReturn(aResponse().withStatus(code))
            )
            connector.get(mrn).futureValue mustBe None
        }
      }

      "should return None when empty object is returned" in {

        server.stubFor(
          get(uri)
            .willReturn(okJson(emptyObject))
        )
        connector.get(mrn).futureValue mustBe None
      }

      "should return None when json is malformed" in {

        server.stubFor(
          get(uri)
            .willReturn(okJson(malFormedJson))
        )
        connector.get(mrn).futureValue mustBe None
      }
    }
  }

}

object UnloadingConnectorSpec {

  private val unloadingJson = JsArray(Seq(JsObject(Map("messages" -> JsString("test"))))).toString

  private val malFormedJson =
    """
      ||{
      |[
      |}
    """.stripMargin

  private val emptyObject: String = JsObject.empty.toString()
  private val mrn = new MovementReferenceNumber("99","IT","9876AB88901209")
  private val uri = s"/common-transit-convention-trader-at-destination/movements/$mrn"

  val responseCodes: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)
}