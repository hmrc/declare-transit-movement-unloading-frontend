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
import javax.inject.Inject
import models.{MovementReferenceNumber, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.{ReferenceDataService, UnloadingPermissionService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.UnloadingSummaryViewModel
import viewModels.sections.Section

import scala.concurrent.ExecutionContext

class UnloadingSummaryController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  unloadingPermissionService: UnloadingPermissionService,
  referenceDataService: ReferenceDataService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      //TODO: Do we need to return UnloadingSummaryViewModel, could just return Seq[Sections]
      val sections: Seq[Section] = unloadingPermissionService.getUnloadingPermission(mrn) match {
        case Some(unloadingPermission) => UnloadingSummaryViewModel()(unloadingPermission).sections
      }

      val redirectUrl = controllers.routes.AnythingElseToReportController.onPageLoad(mrn, NormalMode)
      val json        = Json.obj("mrn" -> mrn, "redirectUrl" -> redirectUrl.url, "sections" -> Json.toJson(sections))

      renderer.render("unloadingSummary.njk", json).map(Ok(_))
  }
}
