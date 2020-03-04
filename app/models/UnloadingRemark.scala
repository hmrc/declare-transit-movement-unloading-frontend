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

package models

case class UnloadingRemark(
  traderAtDestination: String,
  customsOffice: String,
  unloadingRemark: String
  /*
                          ---RESULTS OF CONTROL	9x	D	C210
---SEALS INFO	1x	D	C200
------SEALS ID	9999x	D	R206
---GOODS ITEM	9999x	D	C210 TR0007 TR0011
------PRODUCED DOCUMENTS/CERTIFICATES	99x	D	TR0008
------RESULTS OF CONTROL	199x	D	C210 TR0012 TR0013 TR0014
------CONTAINERS	99x	D	TR0008
------PACKAGES	99x	D	TR0008
------SGI CODES	9x	O	R155 TR0008

 */
)
