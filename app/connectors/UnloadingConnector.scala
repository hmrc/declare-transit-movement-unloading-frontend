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

package connectors

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import models.Movement
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnloadingConnector @Inject()(config: FrontendAppConfig, http: HttpClient) {

  def get()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Option[Movement]] = {

    val url = config.arrivalsBackend

    http
      .GET[Movement](url)
      .map {
        x =>
          Some(x)
      }
      .recover {
        case _ => None
      }
    //TODO: Get get url from config
    //TODO: call get on backend to pull bacxck movement
    //TODO: return movement
    // Some(Movement("test"))
  }

}
