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

package models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ArrivalStatusSpec extends AnyFreeSpec with Matchers {
  "ArrivalStatus.Initialized" - {
    "must transition for specific cases" in {
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Right(ArrivalStatus.ArrivalSubmitted)
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.UnloadingPermission)
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.ArrivalRejected) mustBe Right(ArrivalStatus.ArrivalRejected)
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.UnloadingRemarksRejected) mustBe Right(ArrivalStatus.UnloadingRemarksRejected)
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(
        ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement
      )
    }
    "must not transition for other case" in {
      ArrivalStatus.Initialized.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from Initialized to UnloadingRemarksSubmitted.")
      )
    }
  }

  "ArrivalStatus.ArrivalSubmitted" - {
    "must transition for specific cases" in {
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.UnloadingPermission)
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.ArrivalRejected) mustBe Right(ArrivalStatus.ArrivalRejected)
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.UnloadingRemarksRejected) mustBe Right(ArrivalStatus.UnloadingRemarksRejected)
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(
        ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement
      )
    }
    "must not transition for other case" in {
      ArrivalStatus.ArrivalSubmitted.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from ArrivalSubmitted to ArrivalSubmitted.")
      )
    }
  }

  "ArrivalStatus.UnloadingPermission" - {
    "must transition for specific cases" in {
      ArrivalStatus.UnloadingPermission.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.UnloadingPermission)
      ArrivalStatus.UnloadingPermission.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Right(ArrivalStatus.UnloadingRemarksSubmitted)
      ArrivalStatus.UnloadingPermission.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
    }
    "must not transition for other case" in {
      ArrivalStatus.UnloadingPermission.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from UnloadingPermission to ArrivalSubmitted.")
      )
    }
  }

  "ArrivalStatus.GoodsReleased" - {
    "must transition to self in all cases" in {
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.ArrivalRejected) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.UnloadingRemarksRejected) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(ArrivalStatus.GoodsReleased)
      ArrivalStatus.GoodsReleased.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Right(ArrivalStatus.GoodsReleased)
    }
  }

  "ArrivalStatus.ArrivalRejected" - {
    "must transition for specific cases" in {
      ArrivalStatus.ArrivalRejected.transition(MessageReceivedEvent.ArrivalRejected) mustBe Right(ArrivalStatus.ArrivalRejected)
      ArrivalStatus.ArrivalRejected.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
    }
    "must not transition for other case" in {
      ArrivalStatus.ArrivalRejected.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from ArrivalRejected to UnloadingRemarksSubmitted.")
      )
    }
  }

  "ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement" - {
    "must transition for specific cases" in {
      ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(
        ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement
      )
      ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Right(ArrivalStatus.ArrivalSubmitted)
    }
    "must not transition for other case" in {
      ArrivalStatus.ArrivalXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.GoodsReleased) mustBe Left(
        TransitionError(s"Tried to transition from XMLSubmissionNegativeAcknowledgement to GoodsReleased.")
      )
    }
  }

  "ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement" - {
    "must transition for specific cases" in {
      ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(
        ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement
      )
      ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Right(
        ArrivalStatus.UnloadingRemarksSubmitted
      )
    }
    "must not transition for other case" in {
      ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement.transition(MessageReceivedEvent.GoodsReleased) mustBe Left(
        TransitionError(s"Tried to transition from XMLSubmissionNegativeAcknowledgement to GoodsReleased.")
      )
    }
  }

  "ArrivalStatus.UnloadingRemarksSubmitted" - {
    "must transition for specific cases" in {
      ArrivalStatus.UnloadingRemarksSubmitted.transition(MessageReceivedEvent.UnloadingRemarksRejected) mustBe Right(ArrivalStatus.UnloadingRemarksRejected)
      ArrivalStatus.UnloadingRemarksSubmitted.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.UnloadingPermission)
      ArrivalStatus.UnloadingRemarksSubmitted.transition(MessageReceivedEvent.XMLSubmissionNegativeAcknowledgement) mustBe Right(
        ArrivalStatus.UnloadingRemarksXMLSubmissionNegativeAcknowledgement
      )
      ArrivalStatus.UnloadingRemarksSubmitted.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
    }
    "must not transition for other case" in {
      ArrivalStatus.UnloadingRemarksSubmitted.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from UnloadingRemarksSubmitted to ArrivalSubmitted.")
      )
    }
  }

  "ArrivalStatus.UnloadingRemarksRejected" - {
    "must transition for specific cases" in {
      ArrivalStatus.UnloadingRemarksRejected.transition(MessageReceivedEvent.UnloadingRemarksRejected) mustBe Right(ArrivalStatus.UnloadingRemarksRejected)
      ArrivalStatus.UnloadingRemarksRejected.transition(MessageReceivedEvent.UnloadingRemarksSubmitted) mustBe Right(ArrivalStatus.UnloadingRemarksSubmitted)
      ArrivalStatus.UnloadingRemarksRejected.transition(MessageReceivedEvent.UnloadingPermission) mustBe Right(ArrivalStatus.UnloadingPermission)
      ArrivalStatus.UnloadingRemarksRejected.transition(MessageReceivedEvent.GoodsReleased) mustBe Right(ArrivalStatus.GoodsReleased)
    }
    "must not transition for other case" in {
      ArrivalStatus.UnloadingRemarksRejected.transition(MessageReceivedEvent.ArrivalSubmitted) mustBe Left(
        TransitionError(s"Tried to transition from UnloadingRemarksRejected to ArrivalSubmitted.")
      )
    }
  }
}
