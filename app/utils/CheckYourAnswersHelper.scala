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

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, Index, NormalMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.CheckYourAnswersHelper._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def changesToReport: Option[Row] = userAnswers.get(ChangesToReportPage) map {
    answer =>
      Row(
        key   = Key(msg"changesToReport.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ChangesToReportController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changesToReport.checkYourAnswersLabel"))
          )
        )
      )
  }

  def anythingElseToReport: Option[Row] = userAnswers.get(AnythingElseToReportPage) map {
    answer =>
      Row(
        key   = Key(msg"anythingElseToReport.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AnythingElseToReportController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"anythingElseToReport.checkYourAnswersLabel"))
          )
        )
      )
  }

  def areAnySealsBroken: Option[Row] = userAnswers.get(AreAnySealsBrokenPage) map {
    answer =>
      Row(
        key   = Key(msg"areAnySealsBroken.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AreAnySealsBrokenController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"areAnySealsBroken.checkYourAnswersLabel"))
          )
        )
      )
  }

  def canSealsBeRead: Option[Row] = userAnswers.get(CanSealsBeReadPage) map {
    answer =>
      Row(
        key   = Key(msg"canSealsBeRead.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CanSealsBeReadController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"canSealsBeRead.checkYourAnswersLabel"))
          )
        )
      )
  }

  def newSealNumber(index: Index): Option[Row] = userAnswers.get(NewSealNumberPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"newSealNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.NewSealNumberController.onPageLoad(userAnswers.id, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"newSealNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def grossMassAmount: Option[Row] = userAnswers.get(GrossMassAmountPage) map {
    answer =>
      Row(
        key   = Key(msg"grossMassAmount.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.GrossMassAmountController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"grossMassAmount.checkYourAnswersLabel"))
          )
        )
      )
  }

  def vehicleRegistrationCountry: Option[Row] = userAnswers.get(VehicleRegistrationCountryPage) map {
    answer =>
      Row(
        key   = Key(msg"vehicleRegistrationCountry.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.VehicleRegistrationCountryController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"vehicleRegistrationCountry.checkYourAnswersLabel"))
          ))
      )
  }

  def vehicleNameRegistrationReference: Option[Row] = userAnswers.get(VehicleNameRegistrationReferencePage) map {
    answer =>
      Row(
        key   = Key(msg"vehicleNameRegistrationReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.VehicleNameRegistrationReferenceController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"vehicleNameRegistrationReference.checkYourAnswersLabel"))
          )
        )
      )
  }

  def dateGoodsUnloaded: Option[Row] = userAnswers.get(DateGoodsUnloadedPage) map {
    answer =>
      Row(
        key   = Key(msg"dateGoodsUnloaded.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.format(dateFormatter))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DateGoodsUnloadedController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"dateGoodsUnloaded.checkYourAnswersLabel"))
          )
        )
      )
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
