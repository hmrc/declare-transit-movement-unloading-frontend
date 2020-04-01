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

package services
import com.google.inject.Inject
import connectors.ReferenceDataConnector
import models.reference.Country
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataServiceImpl @Inject()(connector: ReferenceDataConnector) extends ReferenceDataService {

  def getCountryByCode(code: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Country]] = {
//    connector.getCountryList() map {
//      countries =>
//        countries.find(x => x.code.equals(code))
//    }

    connector.getCountryList().flatMap {
      countries =>
        Future.successful(countries.find(x => x.code.equals(code)))
    }
  }

}

trait ReferenceDataService {
  def getCountryByCode(code: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Country]]
}
