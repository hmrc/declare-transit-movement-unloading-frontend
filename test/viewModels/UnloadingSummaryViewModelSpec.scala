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
import cats.data.NonEmptyList
import models.{GoodsItem, Packages, ProducedDocument, Seals, TraderAtDestinationWithEori, UnloadingPermission}
import org.scalatest.{FreeSpec, MustMatchers}

class UnloadingSummaryViewModelSpec extends FreeSpec with MustMatchers {

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

  "UnloadingSummaryViewModel" - {

    "convert UnloadingPermission into UnloadingSummaryViewModel - no seals" in {

      val data = UnloadingSummaryViewModel(unloadingPermission)

      data.sections.length mustBe 0
    }

    "convert UnloadingPermission into UnloadingSummaryViewModel - with seals" in {

      val withSeals = unloadingPermission.copy(seals = Some(Seals(1, Seq("seal 1", "seal 2"))))

      val data = UnloadingSummaryViewModel(withSeals)

      data.sections.length mustBe 1
      data.sections.head.sectionTitle mustBe defined
      data.sections.head.rows.length mustBe 2
    }

    "convert UnloadingPermission into UnloadingSummaryViewModel - with transportIdentity" in {

      val transportIdentity = unloadingPermission.copy(transportIdentity = Some("registration"))

      val data = UnloadingSummaryViewModel(transportIdentity)

      data.sections.length mustBe 1
      data.sections.head.sectionTitle mustBe defined
      data.sections.head.rows.length mustBe 1
    }

    "convert UnloadingPermission into UnloadingSummaryViewModel - with transportCountry and transportIdentity" in {

      val transportCountry = unloadingPermission.copy(transportCountry = Some("registration"), transportIdentity = Some("registration"))

      val data = UnloadingSummaryViewModel(transportCountry)

      data.sections.length mustBe 1
      data.sections.head.sectionTitle mustBe defined
      data.sections.head.rows.length mustBe 2
    }

  }

}
