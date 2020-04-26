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

package controllers

import base.SpecBase
import forms.NewSealNumberFormProvider
import matchers.JsonMatchers
import models.{Index, MovementReferenceNumber, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.NewSealNumberPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class NewSealNumberControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new NewSealNumberFormProvider()
  val form         = formProvider()
  val index        = Index(0)

  lazy val newSealNumberRoute = routes.NewSealNumberController.onPageLoad(mrn, index, NormalMode).url

  "NewSealNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application    = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request        = FakeRequest(GET, newSealNumberRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mrn"  -> mrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "newSealNumber.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers    = UserAnswers(mrn).set(NewSealNumberPage(index), "answer").success.value
      val application    = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request        = FakeRequest(GET, newSealNumberRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "answer"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mrn"  -> mrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "newSealNumber.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "onSubmit" - {
      "must redirect to the next page when valid data is submitted" in {

        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        when(mockUnloadingPermissionService.convertSeals(any())(any(), any()))
          .thenReturn(Future.successful(Some(emptyUserAnswers)))

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[SessionRepository].toInstance(mockSessionRepository)
            )
            .build()

        val request =
          FakeRequest(POST, newSealNumberRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "must return a Bad Request and errors when invalid data is submitted" in {

        when(mockRenderer.render(any(), any())(any()))
          .thenReturn(Future.successful(Html("")))

        val application    = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
        val request        = FakeRequest(POST, newSealNumberRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm      = form.bind(Map("value" -> ""))
        val templateCaptor = ArgumentCaptor.forClass(classOf[String])
        val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

        val expectedJson = Json.obj(
          "form" -> boundForm,
          "mrn"  -> mrn,
          "mode" -> NormalMode
        )

        templateCaptor.getValue mustEqual "newSealNumber.njk"
        jsonCaptor.getValue must containJson(expectedJson)

        application.stop()
      }

      "must redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, newSealNumberRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "must redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, newSealNumberRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "must redirect to the correct page when seals already in the UserAnswers" in {
        val userAnswers           = new UserAnswers(mrn, Json.obj("seals" -> Seq("Seals01")))
        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
              bind[SessionRepository].toInstance(mockSessionRepository)
            )
            .build()

        val request =
          FakeRequest(POST, routes.NewSealNumberController.onPageLoad(mrn, Index(1), NormalMode).url)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "redirect to error page when no UserAnswers returned from unloading permissions service" in {
        val ua = UserAnswers(MovementReferenceNumber("41", "IT", "0211001000782"), Json.obj())
        val application = applicationBuilder(Some(ua))
          .build()

        val request =
          FakeRequest(POST, routes.NewSealNumberController.onPageLoad(mrn, Index(0), NormalMode).url)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        //todo: Test this

        application.stop()
      }
    }
  }
}
