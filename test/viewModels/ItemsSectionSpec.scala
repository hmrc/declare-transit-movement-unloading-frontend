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

import base.SpecBase
import cats.data.NonEmptyList
import models.{GoodsItem, Packages, ProducedDocument, TraderAtDestinationWithEori, UnloadingPermission}
import pages.{ChangesToReportPage, GrossMassAmountPage}
import uk.gov.hmrc.viewmodels.Text.Literal
import utils.UnloadingSummaryRow
import viewModels.sections.Section

class ItemsSectionSpec extends SpecBase {

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

  "ItemsSection" - {
    "Must display" - {
      "Correct Gross mass when no changes have been made" in {

        val grossMassAmount    = unloadingPermission.copy(grossMass = "1000")
        val data: Seq[Section] = ItemsSection(emptyUserAnswers)(grossMassAmount, new UnloadingSummaryRow(emptyUserAnswers))
        data.head.rows.head.value.content mustBe Literal("1000")
        data.head.rows(1).value.content mustBe Literal("Flowers")
      }

      "Correct Gross mass when change has been made" in {
        val grossMassAmount = unloadingPermission.copy(grossMass = "1000")

        val updatedAnswers = emptyUserAnswers
          .set(GrossMassAmountPage, "2000")
          .success
          .value

        val data: Seq[Section] = ItemsSection(updatedAnswers)(grossMassAmount, new UnloadingSummaryRow(emptyUserAnswers))
        data.head.rows.head.value.content mustBe Literal("2000")
        data.head.rows(1).value.content mustBe Literal("Flowers")
      }

      "Correct Comments when change has been made" in {
        val updatedAnswers = emptyUserAnswers
          .set(ChangesToReportPage, "Test")
          .success
          .value

        val data: Seq[Section] = ItemsSection(updatedAnswers)(unloadingPermission, new UnloadingSummaryRow(emptyUserAnswers))
        data.head.rows(2).value.content mustBe Literal("Test")
      }

    }
  }

}
