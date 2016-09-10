package com.github.dozaza

import org.specs2.mutable.Specification

object LogicSpec extends Specification {

  "The 'matchLikelihodd' method" should {
    "be 100% when all attributes match" in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = BuyerPreference(List("male", "tabby"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result must beGreaterThan(0.9999)
    }
  }

  "The 'matchLikelihodd' method" should {
    "be 0% when no attributes match" in {
      val tabby = Kitten(1, List("male", "tabby"))
      val prefs = BuyerPreference(List("femal", "calico"))
      val result = Logic.matchLikelihood(tabby, prefs)
      result must beLessThan(0.0001)
    }
  }

}
