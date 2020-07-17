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

import com.lucidchart.open.xtract.{ParseFailure, XmlReader}
import generators.MessagesModelGenerators
import models.XMLWrites._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers, OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.xml.NodeSeq

class MessageSenderSpec
    extends FreeSpec
    with MustMatchers
    with ScalaCheckPropertyChecks
    with StreamlinedXmlEquality
    with MessagesModelGenerators
    with OptionValues {

  "MessageSender" - {

    "must convert to xml and convert to correct format" in {
      forAll(arbitrary[MessageSender]) {
        messageSender =>
          val expectedResult: NodeSeq =
            <MesSenMES3>{escapeXml(s"${messageSender.environment}-${messageSender.eori}")}</MesSenMES3>

          messageSender.toXml mustEqual expectedResult
      }
    }

    "must deserialize from xml" in {

      forAll(arbitrary[MessageSender]) {
        messageSender =>
          val xml    = messageSender.toXml
          val result = XmlReader.of[MessageSender].read(xml).toOption.value

          result mustBe messageSender
      }
    }

    "must fail to deserialize from xml if invalid format" in {

      val invalidXml = <MesSenMES3>Invalid format</MesSenMES3>
      val result     = XmlReader.of[MessageSender].read(invalidXml)

      result mustBe an[ParseFailure]
    }
  }
}
