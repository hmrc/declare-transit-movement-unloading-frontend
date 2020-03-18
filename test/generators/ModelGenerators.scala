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

package generators

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{choose, listOfN}
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  self: Generators =>

  implicit lazy val arbitraryMovementReferenceNumber: Arbitrary[MovementReferenceNumber] =
    Arbitrary {
      for {
        year    <- Gen.choose(0, 99).map(y => f"$y%02d")
        country <- Gen.pick(2, 'A' to 'Z')
        serial  <- Gen.pick(13, ('A' to 'Z') ++ ('0' to '9'))
      } yield MovementReferenceNumber(year, country.mkString, serial.mkString)
    }

  implicit lazy val arbitrarySensitiveGoodsInformation: Arbitrary[SensitiveGoodsInformation] =
    Arbitrary {
      for {
        goodsCode <- Gen.option(Gen.choose(0: Int, 1000: Int))
        quantity  <- Gen.choose(0: Int, 1000: Int)
      } yield SensitiveGoodsInformation(goodsCode, quantity)
    }

  implicit lazy val arbitraryPackages: Arbitrary[Packages] =
    Arbitrary {
      for {
        marksAndNumberOfPackages <- Gen.option(stringsWithMaxLength(Packages.marksAndNumberPackageLength))
        kindOfPackage            <- stringsWithMaxLength(Packages.kindOfPackageLength)
        numberOfPackages         <- Gen.option(Gen.choose(0: Int, 100: Int))
        numberOfPieces           <- Gen.option(Gen.choose(0: Int, 100: Int))
      } yield Packages(marksAndNumberOfPackages, kindOfPackage, numberOfPackages, numberOfPieces)
    }

  implicit lazy val arbitraryProducedDocument: Arbitrary[ProducedDocument] =
    Arbitrary {
      for {
        documentType            <- stringsWithMaxLength(ProducedDocument.documentTypeLength)
        reference               <- Gen.option(stringsWithMaxLength(ProducedDocument.referenceLength))
        complementOfInformation <- Gen.option(stringsWithMaxLength(ProducedDocument.complementOfInformationLength))
      } yield ProducedDocument(documentType, reference, complementOfInformation)
    }

  implicit lazy val arbitraryGoodsItem: Arbitrary[GoodsItem] =
    Arbitrary {
      for {
        itemNumber                <- choose(min = 1: Int, 100: Int)
        commodityCode             <- Gen.option(stringsWithMaxLength(100: Int))
        description               <- stringsWithMaxLength(Packages.kindOfPackageLength)
        grossMass                 <- Gen.option(stringsWithMaxLength(100: Int)) //todo does this need to be a bigDecimal
        netMass                   <- Gen.option(stringsWithMaxLength(100: Int)) //todo does this need to be a bigDecimal
        producedDocuments         <- nonEmptyListWithMaxSize(10: Int, arbitrary[ProducedDocument])
        containers                <- Gen.option(listWithMaxLength[String](10: Int))
        packages                  <- arbitrary[Packages] //todo should this be a nonEmptySeq
        sensitiveGoodsInformation <- Gen.option(listWithMaxLength[SensitiveGoodsInformation](10: Int))
      } yield GoodsItem(itemNumber, commodityCode, description, grossMass, netMass, producedDocuments, containers, packages, sensitiveGoodsInformation)
    }
}
