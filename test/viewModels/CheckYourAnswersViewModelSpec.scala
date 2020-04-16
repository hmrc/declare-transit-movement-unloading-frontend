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

package viewModels
import java.time.LocalDate

import base.SpecBase
import cats.data.NonEmptyList
import models.{GoodsItem, Packages, ProducedDocument, TraderAtDestinationWithEori, UnloadingPermission, UserAnswers}
import pages.{ChangesToReportPage, DateGoodsUnloadedPage, GrossMassAmountPage, VehicleNameRegistrationReferencePage}
import uk.gov.hmrc.viewmodels.Text.Literal

class CheckYourAnswersViewModelSpec extends SpecBase {

  private val trader =
    TraderAtDestinationWithEori("GB163910077000", Some("The Luggage Carriers"), Some("225 Suedopolish Yard,"), Some("SS8 2BB"), Some(","), Some("GB"))

  private lazy val packages = Packages(Some("Ref."), "BX", Some(1), None)

  private lazy val producedDocuments = ProducedDocument("235", Some("Ref."), None)

  private lazy val goodsItemMandatory = GoodsItem(
    itemNumber                = 1,
    commodityCode             = None,
    description               = "Flowers",
    grossMass                 = Some("1000"),
    netMass                   = Some("999"),
    producedDocuments         = NonEmptyList(producedDocuments, Nil),
    containers                = Seq.empty,
    packages                  = packages,
    sensitiveGoodsInformation = Seq.empty
  )

  private val unloadingPermission = UnloadingPermission(
    movementReferenceNumber = "19IT02110010007827",
    transportIdentity       = None,
    transportCountry        = None,
    numberOfItems           = 1,
    numberOfPackages        = 1,
    grossMass               = "1000",
    traderAtDestination     = trader,
    presentationOffice      = "GB000060",
    seals                   = None,
    goodsItems              = NonEmptyList(goodsItemMandatory, Nil)
  )

  "CheckYourAnswersViewModel" - {

//    "contain no sections if data doesn't exist" in {
//
//      val data = CheckYourAnswersViewModel(emptyUserAnswers, unloadingPermission)
//
//      data.sections mustBe Nil
//    }

    "contain date goods unloaded" in {

      val date = LocalDate.of(2020: Int, 3: Int, 12: Int)

      val userAnswers: UserAnswers = emptyUserAnswers.set(DateGoodsUnloadedPage, date).success.value

      val data = CheckYourAnswersViewModel(userAnswers, unloadingPermission)

      data.sections.length mustBe 2
      data.sections(0).rows(0).value.content mustBe Literal("12 March 2020")
    }

    "contain vehicle registration details" in {
      val userAnswers = emptyUserAnswers.set(VehicleNameRegistrationReferencePage, "test1").success.value
      val data        = CheckYourAnswersViewModel(userAnswers, unloadingPermission)

      data.sections.length mustBe 2
      data.sections(1).rows.head.actions mustBe Nil
    }

    "contain gross mass details" in {
      val userAnswers = emptyUserAnswers.set(GrossMassAmountPage, "500").success.value
      val data        = CheckYourAnswersViewModel(userAnswers, unloadingPermission)

      data.sections.length mustBe 2
      data.sections(1).rows(0).value.content mustBe Literal("500")
    }

    "contain item details" in {
      val userAnswers = emptyUserAnswers
      val data        = CheckYourAnswersViewModel(userAnswers, unloadingPermission)

      data.sections.length mustBe 2
      data.sections(1).rows(1).value.content mustBe Literal("Flowers")
    }

    "contain comments details" in {
      val userAnswers = emptyUserAnswers.set(ChangesToReportPage, "Test comment").success.value
      val data        = CheckYourAnswersViewModel(userAnswers, unloadingPermission)

      data.sections.length mustBe 2
      data.sections(1).rows(2).value.content mustBe Literal("Test comment")
    }
  }
}
