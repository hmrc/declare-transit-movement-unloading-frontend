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

//TODO: Add in mandatory fields and  update paths in xmlReader
case class UnloadingPermission(
  movementReferenceNumber: String,
  transportIdentity: Option[String],
  transportCountry: Option[String],
  numberOfItems: Int,
  numberOfPackages: Int,
  grossMass: String, //TODO:Does this need to be BigDecimal
  // traderAtDestination: TraderAtDestination,
  presentationOffice: String
  // seals: Seq[String],
  //goodsItems: NonEmptyList[GoodsItem]
)

object UnloadingPermission {

  implicit val xmlReader: XmlReader[UnloadingPermission] = (
    (__ \ "HEAHEA" \ "DocNumHEA5").read[String],
    (__ \ "HEAHEA" \ "IdeOfMeaOfTraAtDHEA78").read[String].optional,
    (__ \ "HEAHEA" \ "NatOfMeaOfTraAtDHEA80").read[String].optional,
    (__ \ "HEAHEA" \ "TotNumOfIteHEA305").read[Int],
    (__ \ "HEAHEA" \ "TotNumOfPacHEA306").read[Int],
    (__ \ "HEAHEA" \ "TotGroMasHEA307").read[String],
    (__ \ "CUSOFFPREOFFRES" \ "RefNumRES1").read[String]
  ).mapN(apply _)
}
