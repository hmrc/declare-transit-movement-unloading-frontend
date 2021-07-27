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

package services

import connectors.ReferenceDataConnector
import models.reference.Country
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataServiceSpec extends FreeSpec with ScalaFutures with MustMatchers with MockitoSugar {

  val mockConnector = mock[ReferenceDataConnector]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "ReferenceDataService" - {

    "getCountryByCode should" - {

      "return None if country can't be found" in {

        when(mockConnector.getCountryList()).thenReturn(
          Future.successful(Nil)
        )

        val service = new ReferenceDataServiceImpl(mockConnector)
        service.getCountryByCode(Some("GB")).futureValue mustBe None
      }

      "return None if country code is not passed in" in {

        when(mockConnector.getCountryList()).thenReturn(
          Future.successful(Nil)
        )

        val service = new ReferenceDataServiceImpl(mockConnector)
        service.getCountryByCode(None).futureValue mustBe None
      }

      "return Country if country code exists" in {

        when(mockConnector.getCountryList()).thenReturn(
          Future.successful(
            Seq(
              Country("GB", "United Kingdom"),
              Country("AD", "Andorra")
            )
          )
        )

        val service = new ReferenceDataServiceImpl(mockConnector)
        service.getCountryByCode(Some("GB")).futureValue mustBe Some(
          Country("GB", "United Kingdom")
        )
      }

    }

  }

}
