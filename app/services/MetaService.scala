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

package services
import java.time.LocalDateTime

import com.google.inject.Inject
import config.FrontendAppConfig
import models.messages.{InterchangeControlReference, MessageSender, Meta}

class MetaServiceImpl @Inject()(config: FrontendAppConfig, dateTimeService: DateTimeService) extends MetaService {

  def build(eori: String, interchangeControlReference: InterchangeControlReference): Meta = {
    val messageSender                  = MessageSender(config.environment, eori)
    val currentDateTime: LocalDateTime = dateTimeService.currentDateTime
    val dateOfPreparation              = currentDateTime.toLocalDate
    val timeOfPreparation              = currentDateTime.toLocalTime

    Meta(messageSender, interchangeControlReference, dateOfPreparation, timeOfPreparation)
  }

}

trait MetaService {
  def build(eori: String, interchangeControlReference: InterchangeControlReference): Meta
}