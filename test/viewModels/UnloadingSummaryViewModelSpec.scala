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
import models.{GoodsItem, Packages, ProducedDocument, Seals, TraderAtDestinationWithEori, UnloadingPermission}
import org.scalatest.{FreeSpec, MustMatchers}

class UnloadingSummaryViewModelSpec extends FreeSpec with MustMatchers with SpecBase {

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

  private val transportCountry = None

  "UnloadingSummaryViewModel" - {

    "vehicle section should" - {

      "display transportIdentity" in {

        val transportIdentity = unloadingPermission.copy(transportIdentity = Some("registration"))

        val data = UnloadingSummaryViewModel(emptyUserAnswers, transportCountry)(transportIdentity)

        data.sections.length mustBe 2
        data.sections.head.sectionTitle mustBe defined
        data.sections.head.rows.length mustBe 1
      }

      "display transportCountry" in {

        val unloadingPermissionTransportCountry = unloadingPermission.copy(transportCountry = Some("registration"))

        val data = UnloadingSummaryViewModel(emptyUserAnswers, transportCountry)(unloadingPermissionTransportCountry)

        data.sections.length mustBe 2
        data.sections.head.sectionTitle mustBe defined
        data.sections.head.rows.length mustBe 1
      }

      "display transportCountry and transportIdentity" in {

        val unloadingPermissionTransportCountry = unloadingPermission.copy(transportCountry = Some("registration"), transportIdentity = Some("registration"))

        val data = UnloadingSummaryViewModel(emptyUserAnswers, transportCountry)(unloadingPermissionTransportCountry)

        data.sections.length mustBe 2
        data.sections.head.sectionTitle mustBe defined
        data.sections.head.rows.length mustBe 2
      }

    }

    "items section should" - {

      "display total mass with single item" in {

        val data = UnloadingSummaryViewModel(emptyUserAnswers, transportCountry)(unloadingPermission)

        data.sections.length mustBe 1
        data.sections.head.sectionTitle mustBe defined
        data.sections.head.rows.length mustBe 2
        data.sections.head.rows.head.actions.isEmpty mustBe false
        data.sections.head.rows(1).actions.isEmpty mustBe true
      }

    }

  }

}
