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

import com.lucidchart.open.xtract.{__, XmlReader}
import cats.syntax.all._

sealed trait TraderAtDestination

object TraderAtDestination {

  implicit lazy val xmlReader: XmlReader[TraderAtDestination] = TraderAtDestinationWithEori.xmlReader.or(TraderAtDestinationWithoutEori.xmlReader)

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

}
