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

final case class ProducedDocument(
  documentType: String,
  reference: Option[String],
  complementOfInformation: Option[String]
)

object ProducedDocument {

  implicit val xmlReader: XmlReader[ProducedDocument] = (
    (__ \ "DocTypDC21").read[String],
    (__ \ "DocRefDC23").read[String].optional,
    (__ \ "ComOfInfDC25").read[String].optional
  ).mapN(apply)
}
