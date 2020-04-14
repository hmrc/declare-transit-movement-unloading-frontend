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
import models.{MovementReferenceNumber, UnloadingPermission, UserAnswers}
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json

class UnloadingPermissionServiceSpec extends FreeSpec with MustMatchers with MockitoSugar {
  private val mockConnector = mock[UnloadingConnector]
  private val service       = new UnloadingPermissionServiceImpl(mockConnector)

  "UnloadingPermissionService" - {

    //TODO: This needs more tests adding when we're calling connector
    "must return UnloadingPermission" in {
      service.getUnloadingPermission(MovementReferenceNumber("19IT02110010007827").get) mustBe a[Option[UnloadingPermission]]
    }

    "convertSeals" - {
      "return the same userAnswers when given an ID with no Seals" in {
        val mrn: MovementReferenceNumber = MovementReferenceNumber("22", "IT", "0211001000782")
        val userAnswers                  = UserAnswers(mrn, Json.obj())
        service.convertSeals(userAnswers) mustBe Some(userAnswers)
      }

      "return updated userAnswers when given an ID with seals" in {
        val mrn: MovementReferenceNumber = MovementReferenceNumber("19", "IT", "0211001000782")
        val userAnswers                  = UserAnswers(mrn, Json.obj())
        val userAnswersWithSeals         = UserAnswers(mrn, Json.obj("seals" -> Seq("Seals01", "Seals02")), userAnswers.lastUpdated)
        service.convertSeals(userAnswers) mustBe Some(userAnswersWithSeals)
      }
    }
  }
}
