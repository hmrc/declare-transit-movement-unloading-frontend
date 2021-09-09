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

package viewModels

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.Format._
import viewModels.sections.Section

case class RejectionCheckYourAnswersViewModel(sections: Seq[Section])

object RejectionCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers): RejectionCheckYourAnswersViewModel =
    RejectionCheckYourAnswersViewModel(
      Seq(
        Section(
          Seq(
            vehicleNameRegistrationRejection(userAnswers),
            dateGoodsUnloaded(userAnswers),
            totalNumberOfItems(userAnswers),
            totalNumberOfPackages(userAnswers),
            grossMassAmount(userAnswers)
          ).flatten
        )
      )
    )

  def vehicleNameRegistrationRejection(userAnswers: UserAnswers): Option[Row] = userAnswers.get(VehicleNameRegistrationReferencePage) map {
    answer =>
      Row(
        key = Key(msg"vehicleNameRegistrationReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.VehicleNameRegistrationRejectionController.onPageLoad(userAnswers.id).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"vehicleNameRegistrationReference.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-vehicle-registration-rejection")
          )
        )
      )
  }

  def dateGoodsUnloaded(userAnswers: UserAnswers): Option[Row] = userAnswers.get(DateGoodsUnloadedPage) map {
    answer =>
      Row(
        key = Key(msg"dateGoodsUnloaded.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.format(cyaDateFormatter))),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.DateGoodsUnloadedRejectionController.onPageLoad(userAnswers.id).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"dateGoodsUnloaded.checkYourAnswersLabel")),
            attributes = Map("id" -> "change-date-goods-unloaded")
          )
        )
      )
  }

  def totalNumberOfPackages(userAnswers: UserAnswers): Option[Row] = userAnswers.get(TotalNumberOfPackagesPage) map {
    answer =>
      Row(
        key = Key(msg"totalNumberOfPackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.toString)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.TotalNumberOfPackagesRejectionController.onPageLoad(userAnswers.id).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNumberOfPackages.checkYourAnswersLabel"))
          )
        )
      )
  }

  def totalNumberOfItems(userAnswers: UserAnswers): Option[Row] = userAnswers.get(TotalNumberOfItemsPage) map {
    answer =>
      Row(
        key = Key(msg"totalNumberOfItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.toString)),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.TotalNumberOfItemsController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNumberOfItems.checkYourAnswersLabel"))
          )
        )
      )
  }

  def grossMassAmount(userAnswers: UserAnswers): Option[Row] = userAnswers.get(GrossMassAmountPage) map {
    answer =>
      Row(
        key = Key(msg"grossMassAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.GrossMassAmountRejectionController.onPageLoad(userAnswers.id).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"grossMassAmount.checkYourAnswersLabel"))
          )
        )
      )
  }

}
