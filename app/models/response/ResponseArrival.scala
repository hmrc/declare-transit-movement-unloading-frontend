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

package models.response

import controllers.routes
import models.Arrival
import models.ArrivalId
import models.ArrivalStatus
import models.MongoDateTimeFormats
import models.MovementReferenceNumber
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.OWrites
import play.api.libs.json.Reads

import java.time.LocalDateTime

case class ResponseArrival(arrivalId: ArrivalId, status: ArrivalStatus)

object ResponseArrival {

  def build(arrival: Arrival): ResponseArrival =
    ResponseArrival(
      arrival.arrivalId,
      arrival.status
    )

  val projection: JsObject = Json.obj(
    "_id"    -> 1,
    "status" -> 1
  )

  implicit def reads: Reads[ResponseArrival] =
    json =>
      for {
        arrivalId <- (json \ "_id").validate[ArrivalId]
        status    <- (json \ "status").validate[ArrivalStatus]
      } yield
        ResponseArrival(
          arrivalId,
          status
      )

  implicit val writes: OWrites[ResponseArrival] = Json.writes[ResponseArrival]

}
