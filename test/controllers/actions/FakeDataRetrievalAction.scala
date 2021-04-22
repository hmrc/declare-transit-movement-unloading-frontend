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

import models.{ArrivalId, UserAnswers}
import models.requests.{AuthorisedRequest, IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class FakeDataRetrievalActionProvider(dataToReturn: Option[UserAnswers]) extends DataRetrievalActionProvider {

  def apply(arrivalId: ArrivalId): ActionTransformer[AuthorisedRequest, OptionalDataRequest] =
    new FakeDataRetrievalAction(dataToReturn)
}

class FakeDataRetrievalAction(dataToReturn: Option[UserAnswers]) extends ActionTransformer[AuthorisedRequest, OptionalDataRequest] {

  override protected def transform[A](request: AuthorisedRequest[A]): Future[OptionalDataRequest[A]] =
    Future(OptionalDataRequest(request.request, request.eoriNumber, dataToReturn))

  implicit override protected val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}
