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
import com.lucidchart.open.xtract.{__, XmlReader}
import com.lucidchart.open.xtract.XmlReader._
import cats.syntax.all._
import xml.NonEmptyListOps

final case class GoodsItem(
  itemNumber: Int,
  commodityCode: Option[String],
  description: String,
  grossMass: Option[String], //todo does this need to be a bigDecimal
  netMass: Option[String], //todo does this need to be a bigDecimal
  producedDocuments: NonEmptyList[ProducedDocument],
  containers: Seq[String],
  packages: Packages, //todo should this be a nonEmptySeq
  sensitiveGoodsInformation: Seq[SensitiveGoodsInformation]
)

object GoodsItem {

  val commodityCodeLength = 22
  val descriptionLength   = 280
  val maxDocuments        = 99
  val maxContainers       = 99
  val maxPackages         = 99
  val maxSensitiveGoods   = 9

  implicit val xmlReader: XmlReader[GoodsItem] = (
    (__ \ "IteNumGDS7").read[Int],
    (__ \ "ComCodTarCodGDS10").read[String].optional,
    (__ \ "GooDesGDS23").read[String],
    (__ \ "GroMasGDS46").read[String].optional,
    (__ \ "NetMasGDS48").read[String].optional,
    (__ \ "PRODOCDC2").read[NonEmptyList[ProducedDocument]](NonEmptyListOps.nonEmptyListReader),
    (__ \ "CONNR2" \ "ConNumNR21").read(strictReadSeq[String]), //TODO:Check this is the correct node values
    //TODO: If the above isn't available a Some(Vector()) is returned
    (__ \ "PACGS2").read[Packages], //todo should this be a nonEmptySeq
    (__ \ "SGICODSD2").read(seq[SensitiveGoodsInformation])
  ).mapN(apply)
}
