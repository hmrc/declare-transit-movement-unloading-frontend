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

import com.google.inject.{ImplementedBy, Inject, Singleton}
import config.FrontendAppConfig
import models.{Movement, MovementReferenceNumber}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnloadingConnectorImpl @Inject()(val config: FrontendAppConfig, val http: HttpClient) extends UnloadingConnector {

  /**
    * Connector SHOULD
    * - Consider returning more meaningful responses on failure
    */
  def get(mrn: MovementReferenceNumber)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Option[Seq[Movement]]] = {

    val url = config.arrivalsBackend ++ mrn.toString

    http
      .GET[Seq[Movement]](url)
      .map {
        x =>
          if (x.isEmpty) {
            None
          } else {
            Some(x)
          }
      }
      .recover {
        case _ => None
      }
  }

}

@ImplementedBy(classOf[UnloadingConnectorImpl])
trait UnloadingConnector {
  def get(mrn: MovementReferenceNumber)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Option[Seq[Movement]]]

}
