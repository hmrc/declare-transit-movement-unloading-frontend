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

case class Packages(
  marksAndNumberPackage: Option[String],
  kindOfPackage: String,
  numberOfPackages: Option[Int],
  numberOfPieces: Option[Int]
)

object Packages {

  implicit val xmlReader: XmlReader[Packages] = (
    (__ \ "MarNumOfPacGS21").read[String].optional,
    (__ \ "KinOfPacGS23").read[String],
    (__ \ "NumOfPacGS24").read[Int].optional,
    (__ \ "NumOfPieGS25").read[Int].optional
  ).mapN(apply)
}
