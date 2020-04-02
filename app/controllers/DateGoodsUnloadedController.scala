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

import controllers.actions._
import forms.DateGoodsUnloadedFormProvider
import javax.inject.Inject
import models.{Mode, MovementReferenceNumber, UserAnswers}
import navigation.Navigator
import pages.DateGoodsUnloadedPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import scala.concurrent.{ExecutionContext, Future}

class DateGoodsUnloadedController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: DateGoodsUnloadedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  val form = formProvider()

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(mrn)).async {
    implicit request =>
      val preparedForm = request.userAnswers match {
        case Some(userAnswers) =>
          userAnswers.get(DateGoodsUnloadedPage) match {
            case Some(value) => form.fill(value)
            case None        => form
          }
        case _ => form
      }

      val viewModel = DateInput.localDate(preparedForm("value"))

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "mrn"  -> mrn,
        "date" -> viewModel
      )

      renderer.render("dateGoodsUnloaded.njk", json).map(Ok(_))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(mrn)).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val viewModel = DateInput.localDate(formWithErrors("value"))

            val json = Json.obj(
              "form" -> formWithErrors,
              "mode" -> mode,
              "mrn"  -> mrn,
              "date" -> viewModel
            )

            renderer.render("dateGoodsUnloaded.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.getOrElse(UserAnswers(mrn)).set(DateGoodsUnloadedPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield {
              //TODO: THis needs to redirect to summary page if no seals exist
              Redirect(navigator.nextPage(DateGoodsUnloadedPage, mode, updatedAnswers))
          }
        )
  }
}
