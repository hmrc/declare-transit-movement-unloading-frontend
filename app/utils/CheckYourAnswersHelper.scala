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

package utils

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.Format._

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def totalNumberOfPackages: Option[Row] = userAnswers.get(TotalNumberOfPackagesPage) map {
    answer =>
      Row(
        key   = Key(msg"totalNumberOfPackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.toString)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNumberOfPackagesController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"totalNumberOfPackages.checkYourAnswersLabel")
          )
        )
      )
  }

  def totalNumberOfItems: Option[Row] = userAnswers.get(TotalNumberOfItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"totalNumberOfItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.toString)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNumberOfItemsController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"totalNumberOfItems.checkYourAnswersLabel")
          )
        )
      )
  }

  def changesToReport: Option[Row] = userAnswers.get(ChangesToReportPage) map {
    answer =>
      Row(
        key   = Key(msg"changesToReport.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ChangesToReportController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"changesToReport.checkYourAnswersLabel")
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
            visuallyHiddenText = Some(msg"anythingElseToReport.checkYourAnswersLabel")
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
            visuallyHiddenText = Some(msg"areAnySealsBroken.checkYourAnswersLabel"),
            attributes         = Map("id" -> "change-are-any-seals-broken")
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
            visuallyHiddenText = Some(msg"canSealsBeRead.checkYourAnswersLabel"),
            attributes         = Map("id" -> "change-can-seals-be-read")
          )
        )
      )
  }

  def seals(seals: Seq[String]): Option[Row] = seals match {
    case _ :: _ =>
      Some(
        Row(
          key     = Key(msg"checkYourAnswers.seals.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
          value   = Value(Html(seals.mkString("<br>"))),
          actions = Nil
        ))
    case _ => None
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
            visuallyHiddenText = Some(msg"grossMassAmount.checkYourAnswersLabel")
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
            visuallyHiddenText = Some(msg"vehicleRegistrationCountry.checkYourAnswersLabel")
          ))
      )
  }

  val vehicleNameRegistrationReference: Option[Row] = userAnswers.get(VehicleNameRegistrationReferencePage) map {
    answer =>
      Row(
        key   = Key(msg"vehicleNameRegistrationReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.VehicleNameRegistrationReferenceController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"vehicleNameRegistrationReference.checkYourAnswersLabel")
          )
        )
      )
  }

  def dateGoodsUnloaded: Option[Row] = userAnswers.get(DateGoodsUnloadedPage) map {
    answer =>
      Row(
        key   = Key(msg"dateGoodsUnloaded.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(Literal(answer.format(cyaDateFormatter))),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.DateGoodsUnloadedController.onPageLoad(userAnswers.id, CheckMode).url,
            visuallyHiddenText = Some(msg"dateGoodsUnloaded.visually.hidden"),
            attributes         = Map("id" -> "change-date-goods-unloaded")
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

object CheckYourAnswersHelper
