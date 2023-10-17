/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import play.api.libs.json.Json

import java.time.LocalDateTime

class UserAnswersSpec extends SpecBase {

  private val (instant, dateTime) = (
    "946684800000",
    LocalDateTime.of(2000: Int, 1, 1, 0, 0)
  )

  "must read date format" in {

    val json = Json.parse(s"""
        |{
        |    "_id" : ${arrivalId.value},
        |    "mrn" : "$mrn",
        |    "eoriNumber" : "${eoriNumber.value}",
        |    "data" : {},
        |    "autoData" : {},
        |    "lastUpdated" : {
        |        "$$date" : {
        |            "$$numberLong" : "$instant"
        |        }
        |    }
        |}""".stripMargin)

    val result = json.as[UserAnswers]

    result mustBe UserAnswers(arrivalId, mrn, eoriNumber, Json.obj(), Json.obj(), dateTime)
  }

}
