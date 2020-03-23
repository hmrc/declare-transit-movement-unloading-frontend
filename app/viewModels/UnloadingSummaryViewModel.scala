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
import models.UnloadingPermission
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import utils.UnloadingSumamaryHelper
import viewModels.sections.Section

case class UnloadingSummaryViewModel(sections: Seq[Section])

object UnloadingSummaryViewModel {

  def apply(unloadingPermission: UnloadingPermission): UnloadingSummaryViewModel =
    unloadingPermission.seals match {
      case Some(seals) => {

        val rows: Seq[Row] = seals.SealId.map(UnloadingSumamaryHelper.seals(1, _))

        UnloadingSummaryViewModel(Seq(Section(msg"changeSeal.title", rows)))
      }
      case _ => {

        val transportIdentity: Seq[Row] = unloadingPermission.transportIdentity.map(UnloadingSumamaryHelper.vehicleUsed(_)).toSeq
        val transportCountry: Seq[Row]  = unloadingPermission.transportCountry.map(UnloadingSumamaryHelper.registeredCountry(_)).toSeq

        val transport = transportIdentity ++ transportCountry

        transport match {
          case _ :: _ => {
            val section = Section(msg"vehicleUsed.title", transport)

            UnloadingSummaryViewModel(Seq(section))
          }
          case _ => UnloadingSummaryViewModel(Seq.empty)
        }
      }
    }

}
