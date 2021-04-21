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

package controllers.actions

import config.FrontendAppConfig
import connectors.UnloadingConnector
import models.requests.{AuthorisedRequest, IdentifierRequest}
import models.response.ResponseArrival
import models.{ArrivalId, ArrivalStatus}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{ActionRefiner, Result}
import renderer.Renderer
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckArrivalStatusProvider @Inject()(unloadingConnector: UnloadingConnector, renderer: Renderer, appConfig: FrontendAppConfig)(
  implicit ec: ExecutionContext) {

  def apply(arrivalId: ArrivalId): ActionRefiner[IdentifierRequest, AuthorisedRequest] =
    new ArrivalStatusAction(arrivalId, unloadingConnector, renderer, appConfig)

}

class ArrivalStatusAction(
  arrivalId: ArrivalId,
  unloadingConnector: UnloadingConnector,
  renderer: Renderer,
  appConfig: FrontendAppConfig
)(implicit protected val executionContext: ExecutionContext)
    extends ActionRefiner[IdentifierRequest, AuthorisedRequest] {

  final val validStatus: Seq[ArrivalStatus] = Seq(ArrivalStatus.UnloadingPermission, ArrivalStatus.UnloadingRemarksRejected)

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, AuthorisedRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    unloadingConnector.getArrival(arrivalId).flatMap {
      case Some(responseArrival: ResponseArrival) if !validStatus.contains(responseArrival.status) =>
        renderer
          .render("canNotSendUnloadingRemarks.njk",
                  Json.obj(
                    "arrivalNotifications" -> s"${appConfig.arrivalNotificationsUrl}"
                  ))(request)
          .map(html => Left(BadRequest(html)))

      case Some(responseArrival: ResponseArrival) if validStatus.contains(responseArrival.status) =>
        Future.successful(Right(AuthorisedRequest(request.request, request.eoriNumber)))

      case None =>
        renderer
          .render("declarationNotFound.njk",
                  Json.obj(
                    "arrivalNotifications" -> s"${appConfig.arrivalNotificationsUrl}"
                  ))(request)
          .map(html => Left(NotFound(html)))

    }
  }
}
