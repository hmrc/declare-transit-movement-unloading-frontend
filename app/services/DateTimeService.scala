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
import java.time.LocalDateTime

import com.google.inject.Singleton
import utils.Format

@Singleton
class DateTimeServiceImpl extends DateTimeService {

  @deprecated("Will be removed. Use LocalDateTime.now(clock), with the system clock", "next")
  override def currentDateTime: LocalDateTime = LocalDateTime.now()

  @deprecated("Will be removed. Use LocalDateTime.now(clock), with the system clock and then format", "next")
  def dateFormatted: String = currentDateTime.format(Format.dateFormatter)
}

trait DateTimeService {

  def currentDateTime: LocalDateTime

  def dateFormatted: String
}
