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
import com.lucidchart.open.xtract.XmlReader._

import cats.syntax.all._

final case class GoodsItem(
  itemNumber: Int,
  commodityCode: Option[String],
  description: String,
  grossMass: Option[String], //todo does this need to be a bigDecimal
  netMass: Option[String], //todo does this need to be a bigDecimal
  producedDocuments: Option[Seq[ProducedDocument]], //todo this needs to be nonEmpty
  containers: Option[Seq[String]],
  packages: Packages,
  sensitiveGoodsInformation: Option[Seq[SensitiveGoodsInformation]]
)

object GoodsItem {
  implicit val xmlReader: XmlReader[GoodsItem] = (
    (__ \ "IteNumGDS7").read[Int],
    (__ \ "ComCodTarCodGDS10").read[String].optional,
    (__ \ "GooDesGDS23").read[String],
    (__ \ "GroMasGDS46").read[String].optional,
    (__ \ "NetMasGDS48").read[String].optional,
    (__ \ "PRODOCDC2").read(seq[ProducedDocument]).optional,
    (__ \ "ConNumNR21").read(seq[String]).optional,
    (__ \ "PACGS2").read[Packages],
    (__ \ "gh").read(seq[SensitiveGoodsInformation]).optional //todo find the correct path
  ).mapN(apply)
}
