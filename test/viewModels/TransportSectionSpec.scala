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
import models.reference.Country
import models.{GoodsItem, Packages, ProducedDocument, TraderAtDestinationWithEori, UnloadingPermission}
import pages.{VehicleNameRegistrationReferencePage, VehicleRegistrationCountryPage}
import uk.gov.hmrc.viewmodels.Text.Literal
import viewModels.sections.Section

class TransportSectionSpec extends SpecBase {

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

  "TransportSection" - {

    "Must display" - {
      "correct transport Indentity number when no changes have been made" in {

        val regNumber          = unloadingPermission.copy(transportIdentity = Some("RegNumber1"))
        val data: Seq[Section] = TransportSection(emptyUserAnswers)(regNumber)
        data.head.rows.head.value.content mustBe Literal("RegNumber1")
      }
      "correct transport country when no changes have been made " in {
        val regNumber          = unloadingPermission.copy(transportCountry = Some("France"))
        val data: Seq[Section] = TransportSection(emptyUserAnswers)(regNumber)
        data.head.rows.head.value.content mustBe Literal("France")
      }

      "not return section if identity and country don't exist" in {

        val noTransport = unloadingPermission.copy(transportCountry = None, transportIdentity = None)

        val data: Seq[Section] = TransportSection(emptyUserAnswers)(noTransport)
        data mustBe Nil
      }

    }
    "When items changed from user answers must " - {
      "display correct transport identity when change has been made" in {

        val regNumber = unloadingPermission.copy(transportIdentity = Some("RegNumber1"))

        val updatedUserAnswers = emptyUserAnswers
          .set(VehicleNameRegistrationReferencePage, "RegNumber2")
          .success
          .value

        val data: Seq[Section] = TransportSection(updatedUserAnswers)(regNumber)
        data.head.rows.head.value.content mustBe Literal("RegNumber2")
      }

      "display correct transport vehicle registration country when change has been made" in {

        val regCountry = unloadingPermission.copy(transportCountry = Some("United Kingdom"))

        val updatedUserAnswers = emptyUserAnswers
          .set(VehicleRegistrationCountryPage, Country("valid", "FR", "France"))
          .success
          .value

        val data: Seq[Section] = TransportSection(updatedUserAnswers)(regCountry)
        data.head.rows.head.value.content mustBe Literal("France")
      }
    }

  }
}
