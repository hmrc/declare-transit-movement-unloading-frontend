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
import utils.UnloadingSummaryHelper
import viewModels.sections.Section

case class UnloadingSummaryViewModel(sections: Seq[Section])

object UnloadingSummaryViewModel {

  def apply()(implicit unloadingPermission: UnloadingPermission): UnloadingSummaryViewModel =
    UnloadingSummaryViewModel(sealsSection ++ transportSection ++ itemsSection)

  private def sealsSection()(implicit unloadingPermission: UnloadingPermission): Seq[Section] = unloadingPermission.seals match {
    case Some(seals) =>
      val rows: Seq[Row] = seals.SealId.zipWithIndex.map(x => UnloadingSummaryHelper.seals(Index(x._2), x._1))
      Seq(Section(msg"changeSeal.title", rows))
    case _ => Nil
  }

  private def transportSection()(implicit unloadingPermission: UnloadingPermission): Seq[Section] = {
    val transportIdentity: Seq[Row] = unloadingPermission.transportIdentity.map(UnloadingSummaryHelper.vehicleUsed(_)).toSeq
    val transportCountry: Seq[Row]  = unloadingPermission.transportCountry.map(UnloadingSummaryHelper.registeredCountry(_)).toSeq

    transportIdentity ++ transportCountry match {
      case transport if transport.nonEmpty =>
        Seq(Section(msg"vehicleUsed.title", transport))
      case _ => Nil
    }
  }

  private def itemsSection()(implicit unloadingPermission: UnloadingPermission): Seq[Section] = {
    val grossMassRow: Seq[Row] = Seq(UnloadingSummaryHelper.grossMass(unloadingPermission.grossMass))
    val itemsRow: Seq[Row]     = unloadingPermission.goodsItems.zipWithIndex.map(x => UnloadingSummaryHelper.items(Index(x._2), x._1.description)).toList

    Seq(Section(msg"changeItems.title", grossMassRow ++ itemsRow))
  }

}
