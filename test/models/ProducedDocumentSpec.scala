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

import com.lucidchart.open.xtract.{ParseSuccess, XmlReader}
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq
import scala.xml.Utility.trim

class ProducedDocumentSpec extends FreeSpec with MustMatchers with Generators with ScalaCheckPropertyChecks {

  "ProducedDocument" - {

    "must serialise packages from Xml" in {

      forAll(arbitrary[ProducedDocument]) {
        producedDocument =>
          val reference = producedDocument.reference.map {
            ref =>
              <DocRefDC23>
              {ref}
            </DocRefDC23>
          }

          val complementOfInformation = producedDocument.complementOfInformation.map {
            information =>
              <ComOfInfDC25>
                {information}
              </ComOfInfDC25>
          }

          val expectedResult = {
            <SGICODSD2>
              <DocTypDC21>
                {producedDocument.documentType}
              </DocTypDC21>
              {reference.getOrElse(NodeSeq.Empty)}
              {complementOfInformation.getOrElse(NodeSeq.Empty)}
            </SGICODSD2>
          }

          XmlReader.of[ProducedDocument].read(trim(expectedResult)) mustBe
            ParseSuccess(ProducedDocument(producedDocument.documentType, producedDocument.reference, producedDocument.complementOfInformation))
      }
    }
  }
}
