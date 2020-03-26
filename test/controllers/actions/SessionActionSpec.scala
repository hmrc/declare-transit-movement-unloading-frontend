/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import base.SpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{BodyParsers, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthenticateHeaderParser, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.SessionKeys
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import play.api.libs.json.JsResult.Exception

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionActionSpec extends SpecBase with MockitoSugar {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]

  class Harness(action: IdentifierAction) {

    def onPageLoad() = action {
      request =>
        Results.Ok
    }
  }

  "Session Action" - {

    "when there's no active session" - {

      "must redirect to the unAuthorise" in {

        when(mockAuthConnector.authorise[Enrolments](any(), any())(any(), any())) thenReturn
          Future.failed(AuthenticateHeaderParser.parse(Map("" -> Seq())))

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers)

        val controller = new Harness(sessionAction)

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(controllers.routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "when there is an active session" - {

      "must perform the action" in {

        when(mockAuthConnector.authorise[Enrolments](any(), any())(any(), any())) thenReturn Future.successful(
          Enrolments(Set(Enrolment("HMCE-NCTS-ORG", Seq(EnrolmentIdentifier("VATRegNoTURN", "")), "Active"))))

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new AuthenticatedIdentifierAction(mockAuthConnector, frontendAppConfig, bodyParsers)

        val controller = new Harness(sessionAction)

        val request = fakeRequest.withSession(SessionKeys.sessionId -> "foo")

        val result = controller.onPageLoad()(request)

        status(result) mustBe OK
      }
    }
  }
}
