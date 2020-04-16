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
import models.{UnloadingPermission, UserAnswers}
import pages.VehicleNameRegistrationReferencePage
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.Row
import utils.{CheckYourAnswersHelper, UnloadingSummaryRow}
import viewModels.sections.Section

case class CheckYourAnswersViewModel(sections: Seq[Section])

object CheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, unloadingPermission: UnloadingPermission)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val vehicleNameRegistration: Option[String] = userAnswers.get(VehicleNameRegistrationReferencePage)
    if (vehicleNameRegistration.isEmpty && unloadingPermission.transportIdentity.nonEmpty) {
      userAnswers.set(VehicleNameRegistrationReferencePage, unloadingPermission.transportIdentity.getOrElse(""))

    }
    val checkYourAnswersRow    = new CheckYourAnswersHelper(userAnswers)
    val newUnloadingSummaryRow = new UnloadingSummaryRow(userAnswers)

    val row: Option[Row] = checkYourAnswersRow.dateGoodsUnloaded

    // val vehicleAnswer: Option[String] = SummaryRow.userAnswerString(userAnswers)(VehicleNameRegistrationReferencePage)
    val vehicleAnswer: Option[String] = userAnswers.get(VehicleNameRegistrationReferencePage)
    val transportIdentity: Seq[Row]   = SummaryRow.row(vehicleAnswer)(unloadingPermission.transportIdentity)(newUnloadingSummaryRow.vehicleUsed)

    if (row.nonEmpty) {
      CheckYourAnswersViewModel(Seq(Section(row.toSeq)))
    } else {

      val vehicleNameRegistrationReferenceRow: Option[Row] = checkYourAnswersRow.vehicleNameRegistrationReference
      if (vehicleNameRegistrationReferenceRow.nonEmpty) {
        CheckYourAnswersViewModel(Seq(Section(row.toSeq ++ transportIdentity)))
      } else {
        CheckYourAnswersViewModel(Seq(Section(transportIdentity)))
      }
    }

  }
}
