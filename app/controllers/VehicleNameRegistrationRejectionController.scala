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
import forms.VehicleNameRegistrationReferenceFormProvider
import javax.inject.Inject
import models.{ArrivalId, Mode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.UnloadingRemarksRejectionService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class VehicleNameRegistrationRejectionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  formProvider: VehicleNameRegistrationReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  rejectionService: UnloadingRemarksRejectionService,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(arrivalId: ArrivalId, mode: Mode): Action[AnyContent] = identify.async {
    implicit request =>
      rejectionService.unloadingRemarksRejectionMessage(arrivalId) flatMap {
        case Some(rejectionMessage) if rejectionMessage.errors.length == 1 =>
          rejectionMessage.errors.head.originalAttributeValue match {
            case Some(originalAttrValue) =>
              val json = Json.obj(
                "form"      -> form.fill(originalAttrValue),
                "arrivalId" -> arrivalId,
                "mode"      -> mode
              )
              renderer.render("vehicleNameRegistrationReference.njk", json).map(Ok(_))
            case None => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case None => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def onSubmit(arrivalId: ArrivalId, mode: Mode): Action[AnyContent] = identify.async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"      -> formWithErrors,
              "arrivalId" -> arrivalId,
              "mode"      -> mode
            )

            renderer.render("vehicleNameRegistrationReference.njk", json).map(BadRequest(_))
          },
          value => Future.successful(Redirect(routes.CheckYourAnswersController.onPageLoad(arrivalId)))
        )
  }
}