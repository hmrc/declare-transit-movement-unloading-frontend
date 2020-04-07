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

package viewModels
import java.time.LocalDate

import base.SpecBase
import models.UserAnswers
import pages.DateGoodsUnloadedPage
import uk.gov.hmrc.viewmodels.Text.Literal

class CheckYourAnswersViewModelSpec extends SpecBase {

  "CheckYourAnswersViewModel" - {

    "contain no sections if data doesn't exist" in {

      val data = CheckYourAnswersViewModel(emptyUserAnswers)

      data.sections mustBe Nil
    }

    "contain date goods unloaded" in {

      val date = LocalDate.of(2020: Int, 3: Int, 12: Int)

      val userAnswers: UserAnswers = emptyUserAnswers.set(DateGoodsUnloadedPage, date).success.value

      val data = CheckYourAnswersViewModel(userAnswers)

      data.sections.length mustBe 1
      data.sections.head.rows.head.value.content mustBe Literal("12 March 2020")
    }
  }
}
