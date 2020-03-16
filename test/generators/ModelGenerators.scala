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

  implicit lazy val packages: Arbitrary[Packages] =
    Arbitrary {
      for {
        marksAndNumberOfPackages <- Gen.option(stringsWithMaxLength(23))
        kindOfPackage            <- stringsWithMaxLength(23)
        numberOfPackages         <- Gen.option(Gen.choose(0: Int, 100: Int))
        numberOfPieces           <- Gen.option(Gen.choose(0: Int, 100: Int))
      } yield Packages(marksAndNumberOfPackages, kindOfPackage, numberOfPackages, numberOfPieces)
    }
}
