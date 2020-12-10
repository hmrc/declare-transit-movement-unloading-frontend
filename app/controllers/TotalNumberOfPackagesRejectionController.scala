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
import forms.TotalNumberOfPackagesFormProvider
import javax.inject.Inject
import models.{ArrivalId, UserAnswers}
import pages.TotalNumberOfPackagesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.UnloadingRemarksRejectionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class TotalNumberOfPackagesRejectionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  formProvider: TotalNumberOfPackagesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  rejectionService: UnloadingRemarksRejectionService,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId)).async {
    implicit request =>
      rejectionService.getRejectedValueAsInt(arrivalId, request.userAnswers)(TotalNumberOfPackagesPage) flatMap {
        case Some(originalAttrValue) =>
          val json = Json.obj(
            "form"      -> form.fill(originalAttrValue),
            "arrivalId" -> arrivalId
          )
          renderer.render("totalNumberOfPackages.njk", json).map(Ok(_))
        case None => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def onSubmit(arrivalId: ArrivalId): Action[AnyContent] = (identify andThen getData(arrivalId)).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"      -> formWithErrors,
              "arrivalId" -> arrivalId
            )

            renderer.render("totalNumberOfPackages.njk", json).map(BadRequest(_))
          },
          value =>
            rejectionService.unloadingRemarksRejectionMessage(arrivalId) flatMap {
              case Some(rejectionMessage) =>
                val userAnswers = UserAnswers(arrivalId, rejectionMessage.movementReferenceNumber, request.eoriNumber)
                for {
                  updatedAnswers <- Future.fromTry(userAnswers.set(TotalNumberOfPackagesPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(routes.RejectionCheckYourAnswersController.onPageLoad(arrivalId))

              case _ => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        )
  }

}
