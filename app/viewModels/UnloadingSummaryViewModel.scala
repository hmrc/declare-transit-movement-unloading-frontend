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
import models.{Index, UnloadingPermission}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import utils.UnloadingSumamaryHelper
import viewModels.sections.Section

case class UnloadingSummaryViewModel(sections: Seq[Section])

object UnloadingSummaryViewModel {

  def apply(unloadingPermission: UnloadingPermission): UnloadingSummaryViewModel = {

    val grossMassRow: Seq[Row] =
      Seq(UnloadingSumamaryHelper.grossMass(unloadingPermission.grossMass))

    val itemsRow: Seq[Row] =
      unloadingPermission.goodsItems.zipWithIndex.map(x => UnloadingSumamaryHelper.items(Index(x._2), x._1.description)).toList

    val itemsSection = Seq(Section(msg"changeItems.title", grossMassRow ++ itemsRow))

    val transportIdentity: Seq[Row] = unloadingPermission.transportIdentity.map(UnloadingSumamaryHelper.vehicleUsed(_)).toSeq
    val transportCountry: Seq[Row]  = unloadingPermission.transportCountry.map(UnloadingSumamaryHelper.registeredCountry(_)).toSeq
    val transport: Seq[Row]         = transportIdentity ++ transportCountry
    val transportSection: Seq[Section] = transport match {
      case _ :: _ => {
        Seq(Section(msg"vehicleUsed.title", transport))
      }
      case _ => Nil
    }

    val sealsSection: Seq[Section] = unloadingPermission.seals match {
      case Some(seals) => {
        val rows: Seq[Row] = seals.SealId.zipWithIndex.map(x => UnloadingSumamaryHelper.seals(Index(x._2), x._1)) //TODO: index needs to change
        Seq(Section(msg"changeSeal.title", rows))
      }
      case _ => Nil
    }

    UnloadingSummaryViewModel(sealsSection ++ transportSection ++ itemsSection)
  }

}
