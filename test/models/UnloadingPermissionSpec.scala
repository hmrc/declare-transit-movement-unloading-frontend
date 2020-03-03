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
import org.scalatest.{FreeSpec, MustMatchers}

import scala.xml.XML

class UnloadingPermissionSpec extends FreeSpec with MustMatchers {

  /**
    * SHOULD -
    * Convert xml string into UnloadingPermission
    * convert with only mandatory values
    * convert with optional values
    * can we generate test xml strings?
    */
  val testXml = XML.loadString("<CC043A><messageId>ie047</messageId><DocNumHEA5>19IT02110010007827</DocNumHEA5></CC043A>")

  "UnloadingPermission" - {

    "convert xml string into UnloadingPermission" in {

      XmlReader.of[UnloadingPermission].read(testXml) mustBe
        ParseSuccess(UnloadingPermission("ie047", "19IT02110010007827"))
    }

  }

}
