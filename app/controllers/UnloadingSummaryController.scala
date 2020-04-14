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
import derivable.DeriveNumberOfSeals
import javax.inject.Inject
import models.{Index, MovementReferenceNumber, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.{ReferenceDataService, UnloadingPermissionService, UnloadingPermissionServiceImpl}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.UnloadingSummaryRow
import viewModels.{SealsSection, UnloadingSummaryViewModel}
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
  referenceDataService: ReferenceDataService,
  unloadingPermissionServiceImpl: UnloadingPermissionServiceImpl
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      //TODO: Do we need to return UnloadingSummaryViewModel, could just return Seq[Sections]
      unloadingPermissionService.getUnloadingPermission(mrn) match {
        case Some(unloadingPermission) =>
          val sections                                          = UnloadingSummaryViewModel(request.userAnswers)(unloadingPermission).sections
          implicit val unloadingSummaryRow: UnloadingSummaryRow = new UnloadingSummaryRow(request.userAnswers)

          val sealsSection = SealsSection(request.userAnswers)(unloadingPermission, unloadingSummaryRow)

          val redirectUrl   = controllers.routes.CheckYourAnswersController.onPageLoad(mrn)
          val addCommentUrl = controllers.routes.ChangesToReportController.onPageLoad(mrn, NormalMode)
          val numberOfSeals = request.userAnswers.get(DeriveNumberOfSeals) match {
            case Some(sealsNum) => sealsNum
            case None =>
              unloadingPermissionServiceImpl.convertSeals(request.userAnswers) match {
                case Some(ua) => ua.get(DeriveNumberOfSeals).getOrElse(0)
                case _        => 0
              }
          }
          val addSealUrl = controllers.routes.NewSealNumberController.onPageLoad(mrn, Index(numberOfSeals), NormalMode) //todo add mode and also point to correct seal

          val json = Json.obj(
            "mrn"           -> mrn,
            "redirectUrl"   -> redirectUrl.url,
            "addCommentUrl" -> addCommentUrl.url,
            "addSealUrl"    -> addSealUrl.url,
            "sealsSection"  -> Json.toJson(sealsSection),
            "sections"      -> Json.toJson(sections)
          )
          renderer.render("unloadingSummary.njk", json).map(Ok(_))
      }
  }

}
