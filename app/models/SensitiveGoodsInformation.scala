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

final case class SensitiveGoodsInformation(
  goodsCode: Option[Int],
  quantity: Int
)

object SensitiveGoodsInformation {

  implicit val xmlReader: XmlReader[SensitiveGoodsInformation] = (
    (__ \ "SenGooCodSD22").read[Int].optional,
    (__ \ "SenQuaSD23").read[Int]
  ).mapN(apply)
}
