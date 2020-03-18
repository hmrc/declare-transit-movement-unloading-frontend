package models
import generators.Generators
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class GoodsItemSpec extends FreeSpec with MustMatchers with Generators with ScalaCheckPropertyChecks {

  "GoodsItem" - {

    "must serialize GoodsItem from Xml" in {

      forAll(arbitrary[GoodsItem]) {

      }
    }
  }

}
