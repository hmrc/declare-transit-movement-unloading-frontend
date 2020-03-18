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

package models
import com.lucidchart.open.xtract.{ParseSuccess, XmlReader}
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq
import scala.xml.Utility.trim

class GoodsItemSpec extends FreeSpec with MustMatchers with Generators with ScalaCheckPropertyChecks {

  "GoodsItem" - {

    "must serialize GoodsItem from Xml" in {

      forAll(arbitrary[GoodsItem]) {

        goodsItem =>
          val commodityCode = goodsItem.commodityCode
            .map {
              commodityCode =>
                <ComCodTarCodGDS10>{commodityCode}</ComCodTarCodGDS10>
            }

          val grossMass = goodsItem.grossMass
            .map {
              grossMass =>
                <GroMasGDS46>{grossMass}</GroMasGDS46>
            }

          val netMass = goodsItem.netMass
            .map {
              netMass =>
                <NetMasGDS48>{netMass}</NetMasGDS48>
            }

          val containers = goodsItem.containers
            .map {
              containers =>
                <CONNR2>{containers}</CONNR2>
            }

          val sensitiveGoodsInformation = goodsItem.sensitiveGoodsInformation
            .map {
              sensitiveGoodsInformation =>
                <SGICODSD2>{sensitiveGoodsInformation}</SGICODSD2>
            }

          val expectedResult = {
            <GOOITEGDS>
          <IteNumGDS7>{goodsItem.itemNumber}</IteNumGDS7>
            {commodityCode.getOrElse(NodeSeq.Empty)}
          <GooDesGDS23>{goodsItem.description}</GooDesGDS23>
          {grossMass.getOrElse(NodeSeq.Empty)}
          {netMass.getOrElse(NodeSeq.Empty)}
          <PRODOCDC2>{goodsItem.producedDocuments}</PRODOCDC2>
          {containers.getOrElse(NodeSeq.Empty)}
          <PACGS2>{goodsItem.packages}</PACGS2>
          {sensitiveGoodsInformation.getOrElse((NodeSeq.Empty))}
          </GOOITEGDS>
          }

          XmlReader.of[GoodsItem].read(trim(expectedResult)) mustBe
            ParseSuccess(
              GoodsItem(
                goodsItem.itemNumber,
                goodsItem.commodityCode,
                goodsItem.description,
                goodsItem.grossMass,
                goodsItem.netMass,
                goodsItem.producedDocuments,
                goodsItem.containers,
                goodsItem.packages,
                goodsItem.sensitiveGoodsInformation
              ))

      }
    }
  }

}
