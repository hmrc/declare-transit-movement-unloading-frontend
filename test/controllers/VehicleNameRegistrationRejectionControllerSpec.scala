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

import java.time.LocalDate

import base.SpecBase
import forms.VehicleNameRegistrationReferenceFormProvider
import generators.MessagesModelGenerators
import matchers.JsonMatchers
import models.ErrorType.IncorrectValue
import models.{ErrorPointer, FunctionalError, NormalMode, UnloadingRemarksRejectionMessage}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.UnloadingRemarksRejectionService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class VehicleNameRegistrationRejectionControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers with MessagesModelGenerators {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new VehicleNameRegistrationReferenceFormProvider()
  val form         = formProvider()

  lazy val vehicleNameRegistrationRejectionRoute: String = routes.VehicleNameRegistrationRejectionController.onPageLoad(arrivalId).url

  "VehicleNameRegistrationRejectionController Controller" - {

    "must populate the value from the rejection service original value attribute" in {

      val mockRejectionService = mock[UnloadingRemarksRejectionService]

      val originalValue    = "some reference"
      val errors           = Seq(FunctionalError(IncorrectValue, ErrorPointer("Invalid value"), None, Some(originalValue)))
      val rejectionMessage = UnloadingRemarksRejectionMessage(mrn.toString, LocalDate.now, None, errors)

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
      when(mockRejectionService.unloadingRemarksRejectionMessage(any())(any(), any())).thenReturn(Future.successful(Some(rejectionMessage)))

      val application = applicationBuilder(Some(emptyUserAnswers))
        .overrides(bind[UnloadingRemarksRejectionService].toInstance(mockRejectionService))
        .build()
      val request = FakeRequest(GET, vehicleNameRegistrationRejectionRoute)

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> originalValue))

      val expectedJson = Json.obj(
        "form"      -> filledForm,
        "arrivalId" -> arrivalId,
        "mode"      -> NormalMode
      )

      templateCaptor.getValue mustEqual "vehicleNameRegistrationReference.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must go to technical difficulties when the error attributes are none" in {

      val mockRejectionService = mock[UnloadingRemarksRejectionService]

      val originalValue    = "some reference"
      val errors           = Seq(FunctionalError(IncorrectValue, ErrorPointer("Invalid value"), None, None))
      val rejectionMessage = UnloadingRemarksRejectionMessage(mrn.toString, LocalDate.now, None, errors)

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
      when(mockRejectionService.unloadingRemarksRejectionMessage(any())(any(), any())).thenReturn(Future.successful(Some(rejectionMessage)))

      val application = applicationBuilder(Some(emptyUserAnswers))
        .overrides(bind[UnloadingRemarksRejectionService].toInstance(mockRejectionService))
        .build()
      val request = FakeRequest(GET, vehicleNameRegistrationRejectionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TechnicalDifficultiesController.onPageLoad().url

      application.stop()
    }

    "must go to technical difficulties when there is no rejection message" in {

      val mockRejectionService = mock[UnloadingRemarksRejectionService]

      val originalValue    = "some reference"
      val errors           = Seq(FunctionalError(IncorrectValue, ErrorPointer("Invalid value"), None, Some(originalValue)))
      val rejectionMessage = UnloadingRemarksRejectionMessage(mrn.toString, LocalDate.now, None, errors)

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))
      when(mockRejectionService.unloadingRemarksRejectionMessage(any())(any(), any())).thenReturn(Future.successful(None))

      val application = applicationBuilder(Some(emptyUserAnswers))
        .overrides(bind[UnloadingRemarksRejectionService].toInstance(mockRejectionService))
        .build()
      val request = FakeRequest(GET, vehicleNameRegistrationRejectionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TechnicalDifficultiesController.onPageLoad().url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application    = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request        = FakeRequest(POST, vehicleNameRegistrationRejectionRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"      -> boundForm,
        "arrivalId" -> arrivalId,
        "mode"      -> NormalMode
      )

      templateCaptor.getValue mustEqual "vehicleNameRegistrationReference.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, vehicleNameRegistrationRejectionRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad(arrivalId).url

      application.stop()
    }
  }
}