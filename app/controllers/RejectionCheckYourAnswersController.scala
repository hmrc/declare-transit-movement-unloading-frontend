/*
 * Copyright 2021 HM Revenue & Customs
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

import audit.services.AuditEventSubmissionService
import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalActionProvider, IdentifierAction}
import handlers.ErrorHandler
import models.ArrivalId
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import services.UnloadingRemarksService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.RejectionCheckYourAnswersViewModel
import viewModels.sections.Section

import scala.concurrent.{ExecutionContext, Future}

class RejectionCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  unloadingRemarksService: UnloadingRemarksService,
  val controllerComponents: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  renderer: Renderer,
  auditEventSubmissionService: AuditEventSubmissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val redirectUrl: ArrivalId => Call =
    arrivalId => controllers.routes.ConfirmationController.onPageLoad(arrivalId)

  def onPageLoad(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId) andThen requireData).async {
    implicit request =>
      val viewModel             = RejectionCheckYourAnswersViewModel(request.userAnswers)
      val answers: Seq[Section] = viewModel.sections
      renderer
        .render(
          "rejection-check-your-answers.njk",
          Json.obj("mrn" -> request.userAnswers.mrn, "sections" -> Json.toJson(answers), "arrivalId" -> arrivalId, "redirectUrl" -> redirectUrl(arrivalId).url)
        )
        .map(Ok(_))
  }

  def onSubmit(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId) andThen requireData).async {
    implicit request =>
      unloadingRemarksService.resubmit(arrivalId, request.userAnswers) flatMap {
        case Some(status) =>
          status match {
            case ACCEPTED => {
              auditEventSubmissionService.auditUnloadingRemarks(request.userAnswers, "resubmitUnloadingRemarks")
              Future.successful(Redirect(routes.ConfirmationController.onPageLoad(arrivalId)))
            }
            case UNAUTHORIZED => errorHandler.onClientError(request, UNAUTHORIZED)
            case _            => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case None => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
      }

  }

}
