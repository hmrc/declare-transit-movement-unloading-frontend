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
import models.{Index, UserAnswers}
import models.reference.Country
import pages.{NewSealNumberPage, QuestionPage}
import uk.gov.hmrc.viewmodels.SummaryList.Row

object SummaryRow {

  type SummaryRow = Option[String] => Option[String] => (String => Row) => Seq[Row]

  type GetUserAnswerString  = UserAnswers => QuestionPage[String] => Option[String]
  type GetUserAnswerCountry = UserAnswers => QuestionPage[Country] => Option[String]

  type GetUserAnswerSeals = UserAnswers => NewSealNumberPage.type => Option[String]

  val userAnswerString: GetUserAnswerString = {
    ua => page =>
      ua.get(page)
  }

  val userAnswerCountry: GetUserAnswerCountry = {
    ua => page =>
      ua.get(page) match {
        case Some(x) => Some(x.description)
        case None    => None
      }
  }

  val userAnswerWithIndex: Index => GetUserAnswerSeals = {
    index => ua => page =>
      ua.get(page(index))
  }

  val row: SummaryRow =
    userAnswer =>
      summaryValue =>
        buildRow => {
          (userAnswer, summaryValue) match {
            case (Some(x), _)    => Seq(buildRow(x))
            case (None, Some(x)) => Seq(buildRow(x))
            case (_, _)          => Nil
          }
    }

  val rowWithIndex: Index => SummaryRow =
    index => userAnswer =>
      summaryValue =>
        buildRow => {
          (userAnswer, summaryValue) match {
            case (Some(x), _)    => Seq(buildRow(index)(x))
            case (None, Some(x)) => Seq(buildRow(index)(x))
            case (_, _)          => Nil
          }
    }
}
