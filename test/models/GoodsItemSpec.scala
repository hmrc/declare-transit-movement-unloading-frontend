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
import cats.data.NonEmptyList
import com.lucidchart.open.xtract.{ParseSuccess, XmlReader}
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.{Elem, NodeSeq}
import scala.xml.Utility.trim

class GoodsItemSpec extends FreeSpec with MustMatchers with Generators with ScalaCheckPropertyChecks {

  private def packages(packages: Packages): Elem = {

    val marksAndNumberPackage = packages.marksAndNumberPackage
      .map {
        marksAndNumber =>
          <MarNumOfPacGS21>{marksAndNumber}</MarNumOfPacGS21>
      }

    val numberOfPackage = packages.numberOfPackages
      .map {
        number =>
          <NumOfPacGS24>{number}</NumOfPacGS24>
      }

    val numberOfPieces = packages.numberOfPieces
      .map {
        number =>
          <NumOfPieGS25>{number}</NumOfPieGS25>
      }

    {
      <PACGS2>
        {marksAndNumberPackage.getOrElse(NodeSeq.Empty)}
        <KinOfPacGS23>
        {packages.kindOfPackage}
      </KinOfPacGS23>
        {numberOfPackage.getOrElse(NodeSeq.Empty)}
        {numberOfPieces.getOrElse(NodeSeq.Empty)}
      </PACGS2>
    }
  }

  private def producedDocument(goodsItem: GoodsItem): NonEmptyList[Elem] =
    goodsItem.producedDocuments.map {

      producedDocument =>
        val reference = producedDocument.reference.map {
          ref =>
            <DocRefDC23>{ref}</DocRefDC23>
        }

        val complementOfInformation = producedDocument.complementOfInformation.map {
          information =>
            <ComOfInfDC25>{information}</ComOfInfDC25>
        }

        {
          <PRODOCDC2>
            <DocTypDC21>
              {producedDocument.documentType}
            </DocTypDC21>
            {reference.getOrElse(false)}
            {complementOfInformation.getOrElse(false)}
          </PRODOCDC2>
        }
    }

  private def sensitiveGoodsInformation(goodsItem: GoodsItem) =
    goodsItem.sensitiveGoodsInformation.map {
      sensitiveGoodsInformation =>
        val goodsCode = sensitiveGoodsInformation.goodsCode.map {
          code =>
            <SenGooCodSD22>{code}</SenGooCodSD22>
        }

        <SGICODSD2>
          {goodsCode.getOrElse(NodeSeq.Empty)}
          <SenQuaSD23>
            {sensitiveGoodsInformation.quantity}
          </SenQuaSD23>
        </SGICODSD2>
    }.toList

  "GoodsItem" - {

    "must serialize GoodsItem from Xml" in {

      forAll(arbitrary[GoodsItem]) {

        goodsItem =>
          val commodityCode = goodsItem.commodityCode.map {
            code =>
              <ComCodTarCodGDS10>{code}</ComCodTarCodGDS10>
          }

          val grossMass: Option[Elem] = goodsItem.grossMass.map {
            grossMass =>
              <GroMasGDS46>{grossMass}</GroMasGDS46>
          }

          val netMass: Option[Elem] = goodsItem.netMass.map {
            netMass =>
              <NetMasGDS48>{netMass}</NetMasGDS48>
          }

          val containers: Seq[Elem] = goodsItem.containers.map {
            value =>
              <ConNumNR21>{value}</ConNumNR21>
          }

          val expectedResult = {
            <GOOITEGDS>
              <IteNumGDS7>
                {goodsItem.itemNumber}
              </IteNumGDS7>
                {commodityCode.getOrElse(NodeSeq.Empty)}
              <GooDesGDS23>
                {goodsItem.description}
              </GooDesGDS23>
              {grossMass.getOrElse(NodeSeq.Empty)}
              {netMass.getOrElse(NodeSeq.Empty)}
              {producedDocument(goodsItem).toList}
              {
                containers.map {
                  x => <CONNR2>{x}</CONNR2>
                }
              }
              {packages(goodsItem.packages)}
              {sensitiveGoodsInformation(goodsItem)}
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
              )
            )
      }
    }
  }
}

//ParseSuccess(GoodsItem(66,Some(쨷翄氷㶦ᄵ얹䜿㶉鵎ᒷ崮洗學㺖蕾恴漦릜Ꮱ⡿펥蝵勆⣎촌υ轆茀奠䌃蘩儤﬌닁胗㮥ि鳒簛伇Ὑ鵇牁盜샙큮鰢謆伴䵢ꩌ뵍뽶䍣䑢贐넌옮ﺫ꬐掼辿傠짓藾⨹뼓駻⑓᝻五␛栌੩❟쓼㱵穊鿒돲䴖秀啴펂呀選殘⢟媨遉纈Ồ),ɽ,Some(䓠튠秊馹ʢ綌噛䆃ぱ꼓㸺⇻쉼げꪚ꧜董柂놂),Some(뚂宜崜忤ꓦ䅪헉目嘻셡峦악汼긲㡬#㹋ꅷ嫪꬝㕲筜货ℨ脪＇糐谎汾닁ᤊᘰỠં抍旅숋坖ꂋ퍔샬阮螜︗뜬럏퉩뷕Ȕ),NonEmptyList(ProducedDocument(뮌,Some(☔앀㜃롥߯馫軘兩ז躞忻䥀뱫뻳朊㬶検稠곚쭆),Some(漆痑龅Ⱛ⪦꟞斍ﺑڋ죰ේ넆ﻃ䀇)), ProducedDocument(䃥,Some(뒪ⲭខ框涺ꂴ椀斬ᩒ鈀驣꾫ꇊ坕༫ꎯ坪鲢ￆ崷ː輇컉蜛渚៲쨲ᦳ),Some(ⷔ괓))),List(, ),Packages(Some(䐬睴),焣꫋,Some(10),Some(52)),
// Some(Vector())))
//ParseSuccess(GoodsItem(66,Some(쨷翄氷㶦ᄵ얹䜿㶉鵎ᒷ崮洗學㺖蕾恴漦릜Ꮱ⡿펥蝵勆⣎촌υ轆茀奠䌃蘩儤﬌닁胗㮥ि鳒簛伇Ὑ鵇牁盜샙큮鰢謆伴䵢ꩌ뵍뽶䍣䑢贐넌옮ﺫ꬐掼辿傠짓藾⨹뼓駻⑓᝻五␛栌੩❟쓼㱵穊鿒돲䴖秀啴펂呀選殘⢟媨遉纈Ồ),ɽ,Some(䓠튠秊馹ʢ綌噛䆃ぱ꼓㸺⇻쉼げꪚ꧜董柂놂),Some(뚂宜崜忤ꓦ䅪헉目嘻셡峦악汼긲㡬#㹋ꅷ嫪꬝㕲筜货ℨ脪＇糐谎汾닁ᤊᘰỠં抍旅숋坖ꂋ퍔샬阮螜︗뜬럏퉩뷕Ȕ),NonEmptyList(ProducedDocument(뮌,Some(☔앀㜃롥߯馫軘兩ז躞忻䥀뱫뻳朊㬶検稠곚쭆),Some(漆痑龅Ⱛ⪦꟞斍ﺑڋ죰ේ넆ﻃ䀇)), ProducedDocument(䃥,Some(뒪ⲭខ框涺ꂴ椀斬ᩒ鈀驣꾫ꇊ坕༫ꎯ坪鲢ￆ崷ː輇컉蜛渚៲쨲ᦳ),Some(ⷔ괓))),List(, ),Packages(Some(䐬睴),焣꫋,Some(10),Some(52)),
// None))
