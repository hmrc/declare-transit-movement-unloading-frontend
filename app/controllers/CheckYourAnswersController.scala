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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalActionProvider, IdentifierAction}
import models.MovementReferenceNumber
import models.reference.Country
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import services.UnloadingPermissionService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.{CheckYourAnswersViewModel, UnloadingSummaryViewModel}
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  unloadingPermissionService: UnloadingPermissionService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val redirectUrl: MovementReferenceNumber => Call =
    mrn => controllers.routes.ConfirmationController.onPageLoad(mrn)

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      unloadingPermissionService.getUnloadingPermission(mrn) match {
        case Some(unloadingPermission) => {

          val viewModel = CheckYourAnswersViewModel(request.userAnswers, unloadingPermission)

          val answers: Seq[Section] = viewModel.sections

          renderer
            .render(
              "check-your-answers.njk",
              Json.obj("sections" -> Json.toJson(answers), "redirectUrl" -> redirectUrl(mrn).url)
            )
            .map(Ok(_))
        }
      }
  }
}
