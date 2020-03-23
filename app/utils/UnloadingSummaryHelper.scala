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
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._

object UnloadingSumamaryHelper {

  def seals(index: Int, value: String) =
    Row(
      key   = Key(msg"changeSeal.sealList.label".withArgs(index), classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = "#",
          visuallyHiddenText = Some(msg"changeSeal.sealList.change.hidden".withArgs(index)),
          attributes         = Map("id" -> s"""change-seal-$index""")
        )
      )
    )

  def vehicleUsed(value: String) =
    Row(
      key   = Key(msg"changeVehicle.reference.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = "#",
          visuallyHiddenText = Some(msg"changeVehicle.reference.change.hidden"),
          attributes         = Map("id" -> s"""change-vehicle-reference""")
        )
      )
    )

  def registeredCountry(value: String) =
    Row(
      key   = Key(msg"changeVehicle.registeredCountry.label", classes = Seq("govuk-!-width-one-half")),
      value = Value(lit"$value"),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = "#",
          visuallyHiddenText = Some(msg"changeVehicle.registeredCountry.change.hidden"),
          attributes         = Map("id" -> s"""change-vehicle-reference""")
        )
      )
    )
}
