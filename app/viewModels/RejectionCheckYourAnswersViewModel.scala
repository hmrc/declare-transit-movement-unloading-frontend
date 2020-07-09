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

import controllers.routes
import models.UserAnswers
import pages.VehicleNameRegistrationReferencePage
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._
import utils.CheckYourAnswersHelper
import viewModels.sections.Section

case class RejectionCheckYourAnswersViewModel(sections: Seq[Section])

object RejectionCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): RejectionCheckYourAnswersViewModel = {
    val checkYourAnswersRow = new CheckYourAnswersHelper(userAnswers)
    RejectionCheckYourAnswersViewModel(
      Seq(Section(checkYourAnswersRow.vehicleNameRegistrationReference.toSeq))
    )
  }

  def vehicleNameRegistrationReference(userAnswers: UserAnswers): Option[Row] = userAnswers.get(VehicleNameRegistrationReferencePage) map {
    answer =>
      Row(
        key   = Key(msg"vehicleNameRegistrationReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.VehicleNameRegistrationRejectionController.onPageLoad(userAnswers.id).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"vehicleNameRegistrationReference.checkYourAnswersLabel"))
          )
        )
      )
  }
}
