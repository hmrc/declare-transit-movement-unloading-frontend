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

package models
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import models.XMLWrites._
import models.messages.escapeXml

import scala.xml.NodeSeq
import models.messages.escapeXml

class TraderDestinationSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality {

  "TraderDestination" - {

    "must serialize TraderAtDestinationWithEori to xml" in {
      forAll(arbitrary[TraderAtDestinationWithEori]) {
        trader =>
          val nameNode = trader.name.map(
            name => <NamTRD7>{escapeXml(name)}</NamTRD7>
          )
          val streetNameNode = trader.streetAndNumber.map(
            streetName => <StrAndNumTRD22>{streetName}</StrAndNumTRD22>
          )
          val postCodeNode = trader.postCode.map(
            postcode => <PosCodTRD23>{postcode}</PosCodTRD23>
          )
          val cityNode = trader.city.map(
            city => <CitTRD24>{city}</CitTRD24>
          )
          val countryCodeNode = trader.countryCode.map(
            countryCode => <CouTRD25>{countryCode}</CouTRD25>
          )

          val expectedResult =
            <TRADESTRD>
              {
                nameNode.getOrElse(NodeSeq.Empty) ++
                streetNameNode.getOrElse(NodeSeq.Empty) ++
                postCodeNode.getOrElse(NodeSeq.Empty) ++
                cityNode.getOrElse(NodeSeq.Empty) ++
                countryCodeNode.getOrElse(NodeSeq.Empty)
              }
              <NADLNGRD>EN</NADLNGRD>
              <TINTRD59>{trader.eori}</TINTRD59>
            </TRADESTRD>

          trader.toXml mustEqual expectedResult
      }

    }

    "must serialize TraderAtDestinationWithoutEori to xml" in {
      forAll(arbitrary[TraderAtDestinationWithoutEori]) {
        trader =>
          val expectedResult =
            <TRADESTRD>
              <NamTRD7>{escapeXml(trader.name)}</NamTRD7>
              <StrAndNumTRD22>{trader.streetAndNumber}</StrAndNumTRD22>
              <PosCodTRD23>{trader.postCode}</PosCodTRD23>
              <CitTRD24>{trader.city}</CitTRD24>
              <CouTRD25>{trader.countryCode}</CouTRD25>
              <NADLNGRD>EN</NADLNGRD>
            </TRADESTRD>

          trader.toXml mustEqual expectedResult
      }

    }

  }

}
