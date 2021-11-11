/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import java.time.{Clock, Instant, ZoneId, ZoneOffset}

import base.{AppWithDefaultMockFixtures, SpecBase}

class DataTimeServiceSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val instant: Instant  = Instant.now
  private val stubClock1: Clock = Clock.fixed(Instant.now, ZoneOffset.UTC)
  private val stubClock2: Clock = Clock.fixed(Instant.now, ZoneId.of("Australia/Darwin"))

  val dataTimeService1 = new DateTimeServiceImpl(stubClock1)
  val dataTimeService2 = new DateTimeServiceImpl(stubClock2)

  "currentDateTime" - {

    "must return different times for different system clocks" in {
      !(dataTimeService1.currentDateTime().equals(dataTimeService2.currentDateTime())) mustBe true
    }
  }

  "dateFormatted" - {

    "must return a string of 8 digits" in {
      dataTimeService1.dateFormatted.matches("\\d{8}") mustBe true

    }
  }

}
