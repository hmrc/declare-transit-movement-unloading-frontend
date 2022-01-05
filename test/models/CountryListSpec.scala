/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import generators.Generators
import models.reference.Country
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary

class CountryListSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "fullList" - {
    "return the full list of countries" in {
      forAll(arbitrary[Vector[Country]]) {
        countries =>
          CountryList(countries).fullList must contain theSameElementsAs countries
      }
    }

    "return a list of countries sorted by description when given an unsorted list of countries" in {
      val unsortedCountries = Seq(
        Country("AA", "country5"),
        Country("BB", "country6"),
        Country("CC", "country1"),
        Country("DD", "country3"),
        Country("EE", "country4"),
        Country("FF", "country2")
      )

      val sortedCountries = Seq(
        Country("CC", "country1"),
        Country("FF", "country2"),
        Country("DD", "country3"),
        Country("EE", "country4"),
        Country("AA", "country5"),
        Country("BB", "country6")
      )

      CountryList(unsortedCountries).fullList mustEqual sortedCountries
    }
  }

  "equals" - {
    "returns true if both CountryLists are the same" in {
      val c1 = CountryList(Seq(Country("a", "a")))
      val c2 = CountryList(Seq(Country("a", "a")))
      c1 == c2 mustEqual true
    }

    "returns false if the rhs is not a CountryList" in {
      CountryList(Seq()) == 1 mustEqual false
    }

    "returns false if the rhs has a different list of countries" in {
      val c1 = CountryList(Seq(Country("a", "a")))
      val c2 = CountryList(Seq(Country("b", "b")))
      c1 == c2 mustEqual false
    }

    "returns false if the rhs has a different list of countries with duplicates" in {
      val c1 = CountryList(Seq(Country("a", "a"), Country("a", "a")))
      val c2 = CountryList(Seq(Country("a", "a")))
      c1 == c2 mustEqual false
    }
  }
}
