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
import java.time.format.DateTimeFormatter

import controllers.routes
import models._
import play.api.i18n.Messages
import utils.Date._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import viewModels.sections.Section

case class UnloadingRemarksRejectionViewModel(sections: Seq[Section])

object UnloadingRemarksRejectionViewModel {

  //TODO add logic for multiple rejection errors
  def apply(error: FunctionalError, originalValue: String, arrivalId: ArrivalId)(implicit messages: Messages): UnloadingRemarksRejectionViewModel = {

    val rows: Seq[Row] = error.pointer match {
      case VehicleRegistrationPointer => Seq(vehicleNameRegistrationReference(arrivalId, originalValue))
      case NumberOfPackagesPointer    => Seq(totalNumberOfPackages(arrivalId, originalValue))
      case NumberOfItemsPointer       => Seq(totalNumberOfItems(arrivalId, originalValue))
      case GrossMassPointer           => Seq(grossMassAmount(arrivalId, originalValue))
      case UnloadingDatePointer       => getDate(originalValue).fold[Seq[Row]](Seq.empty)(date => Seq(unloadingDate(arrivalId, date)))
      case DefaultPointer             => Seq.empty
    }
    UnloadingRemarksRejectionViewModel(Seq(Section(rows)))
  }

  private def vehicleNameRegistrationReference(arrivalId: ArrivalId, value: String): Row =
    Row(
      key   = Key(msg"changeVehicle.reference.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.VehicleNameRegistrationRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeVehicle.reference.label")),
          attributes         = Map("id" -> "change-vehicle-registration-rejection")
        )
      )
    )

  def totalNumberOfPackages(arrivalId: ArrivalId, value: String): Row =
    Row(
      key   = Key(msg"changeItems.totalNumberOfPackages.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.TotalNumberOfPackagesRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.totalNumberOfPackages.label"))
        )
      )
    )

  def totalNumberOfItems(arrivalId: ArrivalId, value: String): Row =
    Row(
      key   = Key(msg"changeItems.totalNumberOfItems.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.TotalNumberOfItemsRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.totalNumberOfItems.label"))
        )
      )
    )

  def grossMassAmount(arrivalId: ArrivalId, value: String): Row =
    Row(
      key   = Key(msg"changeItems.grossMass.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.GrossMassAmountRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.grossMass.label"))
        )
      )
    )

  def unloadingDate(arrivalId: ArrivalId, value: LocalDate): Row = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    Row(
      key   = Key(msg"changeItems.dateGoodsUnloaded.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(Literal(value.format(dateFormatter))),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = routes.DateGoodsUnloadedRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.dateGoodsUnloaded.label")),
          attributes         = Map("id" -> "change-date-goods-unloaded")
        )
      )
    )
  }

}
