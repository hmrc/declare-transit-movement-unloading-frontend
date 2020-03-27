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

package services
import connectors.UnloadingConnector
import models.{MovementReferenceNumber, UnloadingPermission}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar

class UnloadingPermissionServiceSpec extends FreeSpec with MustMatchers with MockitoSugar {

  val mockConnector = mock[UnloadingConnector]

  "UnloadingPermissionService" - {

    //TODO: This needs more tests adding when we're calling connector
    "must return UnloadingPermission" in {

      val service = new UnloadingPermissionServiceImpl(mockConnector)

      service.getUnloadingPermission(MovementReferenceNumber("19IT02110010007827").get) mustBe a[Option[UnloadingPermission]]
    }
  }

}
