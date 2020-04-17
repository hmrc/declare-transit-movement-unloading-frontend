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
import models.UserAnswers
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import utils.CheckYourAnswersHelper
import viewModels.sections.Section

case class CheckYourAnswersViewModel(sections: Seq[Section])

object CheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val checkYourAnswersRow = new CheckYourAnswersHelper(userAnswers)

    val row: Option[Row] = checkYourAnswersRow.dateGoodsUnloaded

    if (row.nonEmpty) {
      CheckYourAnswersViewModel(Seq(Section(row.toSeq)))
    } else {
      CheckYourAnswersViewModel(Nil)
    }

  }

}
