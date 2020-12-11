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

import connectors.ReferenceDataConnector
import controllers.actions._
import forms.VehicleRegistrationCountryFormProvider
import javax.inject.Inject
import models.reference.Country
import models.{ArrivalId, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.VehicleRegistrationCountryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class VehicleRegistrationCountryController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: VehicleRegistrationCountryFormProvider,
  referenceDataConnector: ReferenceDataConnector,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(arrivalId: ArrivalId, mode: Mode): Action[AnyContent] = (identify andThen getData(arrivalId) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          val form = formProvider(countries)
          val preparedForm = request.userAnswers.get(VehicleRegistrationCountryPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          renderPage(arrivalId, request.userAnswers.mrn, mode, preparedForm, countries, Results.Ok)
      }

  }

  private def renderPage(arrivalId: ArrivalId, mrn: MovementReferenceNumber, mode: Mode, form: Form[Country], countries: Seq[Country], status: Results.Status)(
    implicit request: Request[AnyContent]): Future[Result] = {
    val json = Json.obj(
      "form"      -> form,
      "mrn"       -> mrn,
      "mode"      -> mode,
      "arrivalId" -> arrivalId,
      "countries" -> countryJsonList(form.value, countries)
    )
    renderer.render("vehicleRegistrationCountry.njk", json).map(status(_))
  }

  private def countryJsonList(value: Option[Country], countries: Seq[Country]): Seq[JsObject] = {
    val countryJsonList = countries.map {
      country =>
        Json.obj("text" -> country.description, "value" -> country.code, "selected" -> value.contains(country))
    }

    Json.obj("value" -> "", "text" -> "") +: countryJsonList
  }

  def onSubmit(arrivalId: ArrivalId, mode: Mode): Action[AnyContent] = (identify andThen getData(arrivalId) andThen requireData).async {
    implicit request =>
      referenceDataConnector.getCountryList() flatMap {
        countries =>
          val form = formProvider(countries)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => {
                renderPage(arrivalId, request.userAnswers.mrn, mode, formWithErrors, countries, Results.BadRequest)
              },
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(VehicleRegistrationCountryPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(VehicleRegistrationCountryPage, mode, updatedAnswers))
            )
      }
  }
}
