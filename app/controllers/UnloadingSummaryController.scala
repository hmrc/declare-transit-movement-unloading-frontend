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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import services.{ReferenceDataService, UnloadingPermissionService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.UnloadingSummaryViewModel

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

  private val redirectUrl: MovementReferenceNumber => Call =
    mrn => controllers.routes.CheckYourAnswersController.onPageLoad(mrn)
  private val addCommentUrl: MovementReferenceNumber => Call =
    mrn => controllers.routes.ChangesToReportController.onPageLoad(mrn, NormalMode)

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      unloadingPermissionService.getUnloadingPermission(mrn) match {
        case Some(unloadingPermission) => {

          referenceDataService.getCountryByCode(unloadingPermission.transportCountry).flatMap {
            transportCountry =>
              val sections = UnloadingSummaryViewModel(request.userAnswers, transportCountry)(unloadingPermission).sections

              val json =
                Json.obj("mrn" -> mrn, "redirectUrl" -> redirectUrl(mrn).url, "addCommentUrl" -> addCommentUrl(mrn).url, "sections" -> Json.toJson(sections))

              renderer.render("unloadingSummary.njk", json).map(Ok(_))
          }

        }
      }
  }
}
