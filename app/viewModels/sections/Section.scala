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

package viewModels.sections
import play.api.i18n.Messages
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{__, OWrites}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Row}
import uk.gov.hmrc.viewmodels.Text
import play.api.libs.functional.syntax._

case class Section(sectionTitle: Option[Text], rows: Seq[Row], link: Option[Action])

object Section {

  def apply(sectionTitle: Text, rows: Seq[Row], action: Action): Section = new Section(Some(sectionTitle), rows, Some(action))

  def apply(sectionTitle: Text, rows: Seq[Row]): Section = new Section(Some(sectionTitle), rows, None)

  def apply(rows: Seq[Row]): Section = new Section(None, rows, None)

  implicit def sectionWrites(implicit messages: Messages): OWrites[Section] =
    (
      (__ \ "sectionTitle").write[Option[Text]] and
        (__ \ "rows").write[Seq[Row]] and
        (__ \ "action").write[Option[Action]]
    )(unlift(Section.unapply))

}
