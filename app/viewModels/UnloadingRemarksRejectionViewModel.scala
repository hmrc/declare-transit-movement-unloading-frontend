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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import controllers.routes
import models._
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels._
import utils.Date._
import viewModels.sections.Section

case class UnloadingRemarksRejectionViewModel(page: String, json: JsObject)

object UnloadingRemarksRejectionViewModel {

  def apply(errors: Seq[FunctionalError], arrivalId: ArrivalId, nctsEnquiriesUrl: String)(implicit
    messages: Messages
  ): Option[UnloadingRemarksRejectionViewModel] = {
    val viewModel: Option[UnloadingRemarksRejectionViewModel] = errors match {
      case error if errors.length == 1 =>
        singleErrorPage(arrivalId, error.head, nctsEnquiriesUrl)
      case `errors` if errors.length > 1 =>
        multipleErrorPage(arrivalId, nctsEnquiriesUrl, errors)
      case _ => None
    }

    viewModel.orElse(defaultErrorPage(arrivalId, errors.headOption, nctsEnquiriesUrl))
  }

  private def multipleErrorPage(arrivalId: ArrivalId, nctsEnquiriesUrl: String, errors: Seq[FunctionalError]): Option[UnloadingRemarksRejectionViewModel] = {
    def json: JsObject =
      Json.obj(
        "errors"                     -> errors,
        "contactUrl"                 -> nctsEnquiriesUrl,
        "arrivalId"                  -> arrivalId,
        "declareUnloadingRemarksUrl" -> routes.IndexController.onPageLoad(arrivalId).url
      )
    Some(UnloadingRemarksRejectionViewModel("unloadingRemarksMultipleErrorsRejection.njk", json))
  }

  private def singleErrorPage(arrivalId: ArrivalId, error: FunctionalError, nctsEnquiriesUrl: String)(implicit
    messages: Messages
  ): Option[UnloadingRemarksRejectionViewModel] = {
    val rowOption: Option[Row] = error.originalAttributeValue flatMap {
      originalValue =>
        error.pointer match {
          case NumberOfPackagesPointer    => Some(totalNumberOfPackages(arrivalId, originalValue))
          case VehicleRegistrationPointer => Some(vehicleNameRegistrationReference(arrivalId, originalValue))
          case NumberOfItemsPointer       => Some(totalNumberOfItems(arrivalId, originalValue))
          case GrossMassPointer           => Some(grossMassAmount(arrivalId, originalValue))
          case UnloadingDatePointer =>
            getDate(originalValue) map (
              date => unloadingDate(arrivalId, date)
            )
          case DefaultPointer(_) => None
        }
    }
    rowOption map {
      row =>
        def json: JsObject =
          Json.obj(
            "sections"   -> Json.toJson(Seq(Section(Seq(row)))),
            "arrivalId"  -> arrivalId,
            "contactUrl" -> nctsEnquiriesUrl
          )
        UnloadingRemarksRejectionViewModel("unloadingRemarksRejection.njk", json)
    }
  }

  private def defaultErrorPage(arrivalId: ArrivalId, error: Option[FunctionalError], nctsEnquiriesUrl: String)(implicit
    messages: Messages
  ): Option[UnloadingRemarksRejectionViewModel] =
    error.flatMap(
      functionalError =>
        functionalError.pointer match {
          case DefaultPointer(_) => multipleErrorPage(arrivalId, nctsEnquiriesUrl, Seq(functionalError))
          case _                 => None
        }
    )

  private def vehicleNameRegistrationReference(arrivalId: ArrivalId, value: String): Row =
    Row(
      key = Key(msg"changeVehicle.reference.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = routes.VehicleNameRegistrationRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeVehicle.reference.label")),
          attributes = Map("id" -> "change-vehicle-registration-rejection")
        )
      )
    )

  def totalNumberOfPackages(arrivalId: ArrivalId, value: String): Row =
    Row(
      key = Key(msg"changeItems.totalNumberOfPackages.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = routes.TotalNumberOfPackagesRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.totalNumberOfPackages.label"))
        )
      )
    )

  def totalNumberOfItems(arrivalId: ArrivalId, value: String): Row =
    Row(
      key = Key(msg"changeItems.totalNumberOfItems.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = routes.TotalNumberOfItemsRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.totalNumberOfItems.label"))
        )
      )
    )

  def grossMassAmount(arrivalId: ArrivalId, value: String): Row =
    Row(
      key = Key(msg"changeItems.grossMass.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content = msg"site.edit",
          href = routes.GrossMassAmountRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.grossMass.label"))
        )
      )
    )

  def unloadingDate(arrivalId: ArrivalId, value: LocalDate): Row = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    Row(
      key = Key(msg"changeItems.dateGoodsUnloaded.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(Literal(value.format(dateFormatter))),
      actions = List(
        Action(
          content = msg"site.edit",
          href = routes.DateGoodsUnloadedRejectionController.onPageLoad(arrivalId).url,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"changeItems.dateGoodsUnloaded.label")),
          attributes = Map("id" -> "change-date-goods-unloaded")
        )
      )
    )
  }

}
