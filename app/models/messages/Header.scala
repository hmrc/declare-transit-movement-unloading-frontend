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

package models.messages
import models.{LanguageCode, LanguageCodeEnglish, XMLWrites}

import scala.xml.NodeSeq

case class Header(
  movementReferenceNumber: String,
  transportIdentity: Option[String],
  transportCountry: Option[String],
  numberOfItems: Int,
  numberOfPackages: Int,
  grossMass: String
)

object Header {

  implicit def writes: XMLWrites[Header] = XMLWrites[Header] {
    header =>
      <HEAHEA>
        <DocNumHEA5>{escapeXml(header.movementReferenceNumber)}</DocNumHEA5>
        {
          header.transportIdentity.fold(NodeSeq.Empty) { transportIdentity =>
            <IdeOfMeaOfTraAtDHEA78>{escapeXml(transportIdentity)}</IdeOfMeaOfTraAtDHEA78>
            <IdeOfMeaOfTraAtDHEA78LNG>{LanguageCodeEnglish.code}</IdeOfMeaOfTraAtDHEA78LNG>
          }
        }
        {
          header.transportCountry.fold(NodeSeq.Empty) { transportCountry =>
            <NatOfMeaOfTraAtDHEA80>{escapeXml(transportCountry)}</NatOfMeaOfTraAtDHEA80>
          }
        }
        <TotNumOfIteHEA305>{header.numberOfItems}</TotNumOfIteHEA305>
        <TotNumOfPacHEA306>{header.numberOfPackages}</TotNumOfPacHEA306>
        <TotGroMasHEA307>{escapeXml(header.grossMass)}</TotGroMasHEA307>
      </HEAHEA>
  }
}