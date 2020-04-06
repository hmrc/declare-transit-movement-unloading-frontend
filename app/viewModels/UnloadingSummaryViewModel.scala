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
import models.{Index, UnloadingPermission, UserAnswers}
import pages.{GrossMassAmountPage, NewSealNumberPage, VehicleNameRegistrationReferencePage, VehicleRegistrationCountryPage}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import utils.UnloadingSummaryRow
import viewModels.sections.Section

case class UnloadingSummaryViewModel(sections: Seq[Section])

object UnloadingSummaryViewModel {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission): UnloadingSummaryViewModel = {

    implicit val unloadingSummaryRow: UnloadingSummaryRow = new UnloadingSummaryRow(userAnswers)

    UnloadingSummaryViewModel(SealsSection(userAnswers) ++ TransportSection(userAnswers) ++ ItemsSection(userAnswers))
  }

}

object SealsSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission, unloadingSummaryRow: UnloadingSummaryRow): Seq[Section] =
    unloadingPermission.seals
      .map {
        seals =>
          val rows: Seq[Row] = seals.SealId.zipWithIndex.map(
            unloadingPermissionValue => {
              val sealAnswer = SummaryRow.userAnswerWithIndex(Index(unloadingPermissionValue._2))(userAnswers)(NewSealNumberPage)
              SummaryRow.rowWithIndex(Index(unloadingPermissionValue._2))(sealAnswer)(unloadingPermissionValue._1)(unloadingSummaryRow.seals)
            }
          )
          Seq(Section(msg"changeSeal.title", rows))
      }
      .getOrElse(Nil)
}

object TransportSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission, unloadingSummaryRow: UnloadingSummaryRow): Seq[Section] = {

    val vehicleAnswer: Option[String] = SummaryRow.userAnswerString(userAnswers)(VehicleNameRegistrationReferencePage)
    val transportIdentity: Seq[Row]   = SummaryRow.row(vehicleAnswer)(unloadingPermission.transportIdentity)(unloadingSummaryRow.vehicleUsed)

    val countryAnswer: Option[String] = SummaryRow.userAnswerCountry(userAnswers)(VehicleRegistrationCountryPage)
    val transportCountry: Seq[Row]    = SummaryRow.row(countryAnswer)(unloadingPermission.transportCountry)(unloadingSummaryRow.registeredCountry)

    transportIdentity ++ transportCountry match {
      case transport if transport.nonEmpty =>
        Seq(Section(msg"vehicleUsed.title", transport))
      case _ => Nil
    }
  }
}

object ItemsSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission, unloadingSummaryRow: UnloadingSummaryRow): Seq[Section] = {
    val grossMassAnswer: Option[String] = SummaryRow.userAnswerString(userAnswers)(GrossMassAmountPage)
    val grossMassRow: Seq[Row]          = SummaryRow.row(grossMassAnswer)(Some(unloadingPermission.grossMass))(unloadingSummaryRow.grossMass)

    val itemsRow: NonEmptyList[Row] = unloadingPermission.goodsItems.zipWithIndex
      .map {
        unloadingPermissionValue =>
          {
            val itemAnswer = None //TODO: Call get on UserAnswers when this is available
            SummaryRow.rowWithIndex(Index(unloadingPermissionValue._2))(itemAnswer)(unloadingPermissionValue._1.description)(unloadingSummaryRow.items)
          }
      }

    Seq(Section(msg"changeItems.title", grossMassRow ++ itemsRow.toList))
  }
}
