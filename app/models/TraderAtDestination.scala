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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.messages.escapeXml

import scala.xml.NodeSeq

sealed trait TraderAtDestination

object TraderAtDestination {

  implicit lazy val xmlReader: XmlReader[TraderAtDestination] =
    TraderAtDestinationWithEori.xmlReader.or(TraderAtDestinationWithoutEori.xmlReader)
}

final case class TraderAtDestinationWithEori(
  eori: String,
  name: Option[String],
  streetAndNumber: Option[String],
  postCode: Option[String],
  city: Option[String],
  countryCode: Option[String]
) extends TraderAtDestination

object TraderAtDestinationWithEori {

  implicit val xmlReader: XmlReader[TraderAtDestinationWithEori] = (
    (__ \ "TINTRD59").read[String],
    (__ \ "NamTRD7").read[String].optional,
    (__ \ "StrAndNumTRD22").read[String].optional,
    (__ \ "PosCodTRD23").read[String].optional,
    (__ \ "CitTRD24").read[String].optional,
    (__ \ "CouTRD25").read[String].optional
  ).mapN(apply)

  object Constants {
    val eoriLength            = 17
    val nameLength            = 35
    val streetAndNumberLength = 35
    val postCodeLength        = 9
    val cityLength            = 35
    val countryCodeLength     = 2
  }

  implicit def writes: XMLWrites[TraderAtDestinationWithEori] = XMLWrites[TraderAtDestinationWithEori] {
    trader =>
      <TRADESTRD>
        {
          trader.name.fold(NodeSeq.Empty) {
            name =>
              <NamTRD7>{escapeXml(name)}</NamTRD7>
          } ++
          trader.streetAndNumber.fold(NodeSeq.Empty) {
            streetAndNumber =>
              <StrAndNumTRD22>{streetAndNumber}</StrAndNumTRD22>
          } ++
          trader.postCode.fold(NodeSeq.Empty) {
            postCode =>
              <PosCodTRD23>{postCode}</PosCodTRD23>
          } ++
          trader.city.fold(NodeSeq.Empty) {
            city =>
              <CitTRD24>{city}</CitTRD24>
          } ++
          trader.countryCode.fold(NodeSeq.Empty) {
            countryCode =>
              <CouTRD25>{countryCode}</CouTRD25>
          }
        }
        <NADLNGRD>{LanguageCodeEnglish.code}</NADLNGRD>
        <TINTRD59>{trader.eori}</TINTRD59>
      </TRADESTRD>
  }
}

final case class TraderAtDestinationWithoutEori(
  name: String,
  streetAndNumber: String,
  postCode: String,
  city: String,
  countryCode: String
) extends TraderAtDestination

object TraderAtDestinationWithoutEori {

  implicit val xmlReader: XmlReader[TraderAtDestinationWithoutEori] = (
    (__ \ "NamTRD7").read[String],
    (__ \ "StrAndNumTRD22").read[String],
    (__ \ "PosCodTRD23").read[String],
    (__ \ "CitTRD24").read[String],
    (__ \ "CouTRD25").read[String]
  ).mapN(apply)

  object Constants {
    val nameLength            = 35
    val streetAndNumberLength = 35
    val postCodeLength        = 9
    val cityLength            = 35
    val countryCodeLength     = 2
  }

  implicit def writes: XMLWrites[TraderAtDestinationWithoutEori] = XMLWrites[TraderAtDestinationWithoutEori] {
    trader =>
      <TRADESTRD>
        <NamTRD7>{escapeXml(trader.name)}</NamTRD7>
        <StrAndNumTRD22>{trader.streetAndNumber}</StrAndNumTRD22>
        <PosCodTRD23>{trader.postCode}</PosCodTRD23>
        <CitTRD24>{trader.city}</CitTRD24>
        <CouTRD25>{trader.countryCode}</CouTRD25>
        <NADLNGRD>{LanguageCodeEnglish.code}</NADLNGRD>
      </TRADESTRD>
  }

}
