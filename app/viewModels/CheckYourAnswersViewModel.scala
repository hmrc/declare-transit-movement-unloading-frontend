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
import cats.data.NonEmptyList
import controllers.routes
import models.reference.Country
import models.{Index, Mode, MovementReferenceNumber, UnloadingPermission, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import utils.{CheckYourAnswersHelper, UnloadingSummaryRow}
import viewModels.sections.Section
import uk.gov.hmrc.viewmodels._

case class CheckYourAnswersViewModel(sections: Seq[Section])

object CheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, unloadingPermission: UnloadingPermission, summaryTransportCountry: Option[Country])(
    implicit messages: Messages): CheckYourAnswersViewModel = {

    val checkYourAnswersRow           = new CheckYourAnswersHelper(userAnswers)
    val rowGoodsUnloaded: Option[Row] = checkYourAnswersRow.dateGoodsUnloaded
    val unloadingSummaryRow           = new UnloadingSummaryRow(userAnswers)

    val rowCanSealsBeRead: Option[Row]    = checkYourAnswersRow.canSealsBeRead
    val rowAreAnySealsBroken: Option[Row] = checkYourAnswersRow.areAnySealsBroken

    val seals: Option[Row] = checkYourAnswersRow.seals

    val transportIdentityAnswer: Option[String] = userAnswers.get(VehicleNameRegistrationReferencePage)
    val transportIdentity: Seq[Row]             = SummaryRow.row(transportIdentityAnswer)(unloadingPermission.transportIdentity)(unloadingSummaryRow.vehicleUsedCYA)

    val transportCountryDescription: Option[String] = summaryTransportCountry match {
      case Some(country) => Some(country.description)
      case None          => unloadingPermission.transportCountry
    }

    val countryAnswer: Option[String] = SummaryRow.userAnswerCountry(userAnswers)(VehicleRegistrationCountryPage)
    val transportCountry: Seq[Row]    = SummaryRow.row(countryAnswer)(transportCountryDescription)(unloadingSummaryRow.registeredCountryCYA)

    val grossMassAnswer: Option[String] = userAnswers.get(GrossMassAmountPage)
    val grossMass: Seq[Row]             = SummaryRow.row(grossMassAnswer)(Some(unloadingPermission.grossMass))(unloadingSummaryRow.grossMassCYA)

    val itemsRow: NonEmptyList[Row] = SummaryRow.rowGoodsItems(unloadingPermission.goodsItems)(userAnswers)(unloadingSummaryRow.items)

    val commentsAnswer: Option[String] = SummaryRow.userAnswerString(userAnswers)(ChangesToReportPage)
    val commentsRow: Seq[Row]          = SummaryRow.row(commentsAnswer)(None)(unloadingSummaryRow.commentsCYA)

    CheckYourAnswersViewModel(
      Seq(
        Section(rowGoodsUnloaded.toSeq),
        Section(msg"checkYourAnswers.seals.subHeading", (rowCanSealsBeRead ++ rowAreAnySealsBroken).toSeq),
        Section(msg"checkYourAnswers.subHeading",
                buildRows(seals.toSeq ++ transportIdentity ++ transportCountry ++ grossMass ++ itemsRow.toList ++ commentsRow, userAnswers.id))
      ))
  }

  private def buildRows(rows: Seq[Row], mrn: MovementReferenceNumber): Seq[Row] = rows match {
    case head :: tail => {
      val changeAction = head.copy(
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.UnloadingSummaryController.onPageLoad(mrn).url,
            visuallyHiddenText = Some(msg"checkYourAnswers.changeItems.hidden"),
            attributes         = Map("id" -> s"""change-answers""")
          )))

      Seq(changeAction) ++ tail
    }
    case _ => rows

  }
}
