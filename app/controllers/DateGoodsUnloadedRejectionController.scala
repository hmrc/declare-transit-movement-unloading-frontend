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

import controllers.actions._
import forms.DateGoodsUnloadedFormProvider
import javax.inject.Inject
import models.{ArrivalId, UserAnswers}
import navigation.NavigatorUnloadingPermission
import pages.DateGoodsUnloadedPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.{UnloadingPermissionService, UnloadingRemarksRejectionService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import scala.concurrent.{ExecutionContext, Future}

class DateGoodsUnloadedRejectionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorUnloadingPermission,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  formProvider: DateGoodsUnloadedFormProvider,
  rejectionService: UnloadingRemarksRejectionService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  unloadingPermissionService: UnloadingPermissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def form: Form[LocalDate] = formProvider()

  def onPageLoad(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId)).async {
    implicit request =>
      rejectionService.getRejectedValueAsDate(arrivalId, request.userAnswers)(DateGoodsUnloadedPage) flatMap {
        case Some(originalValue) =>
          val preparedForm = form.fill(originalValue)
          val viewModel    = DateInput.localDate(preparedForm("value"))

          val json = Json.obj(
            "form"      -> preparedForm,
            "arrivalId" -> arrivalId,
            "date"      -> viewModel
          )

          renderer.render("dateGoodsUnloaded.njk", json).map(Ok(_))
        case _ => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def onSubmit(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId)).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val viewModel = DateInput.localDate(formWithErrors("value"))

            val json = Json.obj(
              "form"      -> formWithErrors,
              "arrivalId" -> arrivalId,
              "date"      -> viewModel
            )

            renderer.render("dateGoodsUnloaded.njk", json).map(BadRequest(_))
          },
          value =>
            rejectionService.unloadingRemarksRejectionMessage(arrivalId) flatMap {
              case Some(rejectionMessage) =>
                val userAnswers = UserAnswers(arrivalId, rejectionMessage.movementReferenceNumber, request.eoriNumber)
                for {
                  updatedAnswers <- Future.fromTry(userAnswers.set(DateGoodsUnloadedPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(routes.RejectionCheckYourAnswersController.onPageLoad(arrivalId))

              case _ => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        )
  }
}