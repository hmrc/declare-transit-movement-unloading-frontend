package models

import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.{AnyFreeSpec => FreeSpec}
import org.scalatest.matchers.must.{Matchers => MustMatchers}
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class $className$Spec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with Generators {

  "$className$" - {

    "must deserialise valid values" in {

      val gen = arbitrary[$className$]

      forAll(gen) {
        $className;format="decap"$ =>

          JsString($className;format="decap"$.toString).validate[$className$].asOpt.value mustEqual $className;format="decap"$
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!$className$.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[$className$] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[$className$]

      forAll(gen) {
        $className;format="decap"$ =>

          Json.toJson($className;format="decap"$) mustEqual JsString($className;format="decap"$.toString)
      }
    }
  }
}
