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
import models.{Index, UnloadingPermission, UserAnswers}
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._
import utils.UnloadingSummaryHelper
import viewModels.sections.Section

case class UnloadingSummaryViewModel(sections: Seq[Section])

object UnloadingSummaryViewModel {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission): UnloadingSummaryViewModel =
    UnloadingSummaryViewModel(SealsSection(userAnswers) ++ TransportSection(userAnswers) ++ ItemsSection(userAnswers))

}

object SealsSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission): Seq[Section] = {
    val helper = new UnloadingSummaryHelper(userAnswers)
    unloadingPermission.seals match {
      case Some(seals) => {
        val rows: Seq[Row] = seals.SealId.zipWithIndex.map(x => helper.seals(Index(x._2), x._1))
        Seq(Section(msg"changeSeal.title", rows))
      }
      case _ => Nil
    }
  }
}

object TransportSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission): Seq[Section] = {
    val helper                      = new UnloadingSummaryHelper(userAnswers)
    val transportIdentity: Seq[Row] = unloadingPermission.transportIdentity.map(helper.vehicleUsed).toSeq
    val transportCountry: Seq[Row]  = unloadingPermission.transportCountry.map(helper.registeredCountry).toSeq

    transportIdentity ++ transportCountry match {
      case transport if transport.nonEmpty =>
        Seq(Section(msg"vehicleUsed.title", transport))
      case _ => Nil
    }

  }
}

object ItemsSection {

  def apply(userAnswers: UserAnswers)(implicit unloadingPermission: UnloadingPermission): Seq[Section] = {
    val helper                 = new UnloadingSummaryHelper(userAnswers)
    val grossMassRow: Seq[Row] = Seq(helper.grossMass(unloadingPermission.grossMass))
    val itemsRow: Seq[Row]     = unloadingPermission.goodsItems.zipWithIndex.map(x => helper.items(Index(x._2), x._1.description)).toList

    Seq(Section(msg"changeItems.title", grossMassRow ++ itemsRow))
  }
}
