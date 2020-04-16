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

package utils
import models.{CheckMode, Index, MovementReferenceNumber, NormalMode, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._

class UnloadingSummaryRow(userAnswers: UserAnswers) {

  val seals: (Index, String) => Row = {
    (index, value) =>
      Row(
        key   = Key(msg"changeSeal.sealList.label".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$value"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.routes.NewSealNumberController.onPageLoad(mrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"changeSeal.sealList.change.hidden".withArgs(index.display)),
            attributes         = Map("id" -> s"""change-seal-${index.position}""")
          )
        )
      )
  }

  val items: (Index, String) => Row = {
    (index, value) =>
      Row(
        key     = Key(msg"changeItem.itemList.label".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$value"),
        actions = Nil
      )
  }

  val vehicleUsed: String => Row = {
    value =>
      Row(
        key   = Key(msg"changeVehicle.reference.label", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$value"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.routes.VehicleNameRegistrationReferenceController.onPageLoad(mrn, CheckMode).url,
            visuallyHiddenText = Some(msg"changeVehicle.reference.change.hidden"),
            attributes         = Map("id" -> s"""change-vehicle-reference""")
          )
        )
      )
  }

  val vehicleUsedCYA: String => Row = {
    value =>
      Row(
        key     = Key(msg"changeVehicle.reference.label", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$value"),
        actions = Nil
      )
  }

  val registeredCountry: String => Row = {
    value =>
      Row(
        key   = Key(msg"changeVehicle.registeredCountry.label", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$value"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.routes.VehicleRegistrationCountryController.onPageLoad(mrn, CheckMode).url,
            visuallyHiddenText = Some(msg"changeVehicle.registeredCountry.change.hidden"),
            attributes         = Map("id" -> s"""change-vehicle-reference""")
          )
        )
      )
  }

  val grossMass: String => Row = {
    value =>
      Row(
        key   = Key(msg"changeItems.grossMass.label", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$value"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.routes.GrossMassAmountController.onPageLoad(mrn, CheckMode).url,
            visuallyHiddenText = Some(msg"changeItems.grossMass.change.hidden"),
            attributes         = Map("id" -> s"""change-gross-mass""")
          )
        )
      )
  }

  val comments: String => Row = {
    value =>
      Row(
        key   = Key(msg"changeItems.comments.label", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$value"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = controllers.routes.ChangesToReportController.onPageLoad(mrn, NormalMode).url,
            visuallyHiddenText = Some(msg"changeItems.comments.change.hidden"),
            attributes         = Map("id" -> s"""change-comments""")
          ),
          Action(
            content            = msg"site.delete",
            href               = controllers.routes.ConfirmRemoveCommentsController.onPageLoad(mrn, NormalMode).url,
            visuallyHiddenText = Some(msg"changeItems.comments.remove.hidden"),
            attributes         = Map("id" -> s"""remove-comment""")
          )
        )
      )
  }

  def mrn: MovementReferenceNumber = userAnswers.id
}
