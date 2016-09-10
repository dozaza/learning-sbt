package com.github.dozaza


object Logic {

  def matchLikelihood(kitten: Kitten, buyer: BuyerPreference): Double = {
    val matches = buyer.attributes.map{ kitten.attributes.contains }
    val nums = matches.map { b => if (b) 1.0 else 0.0 }
    nums.sum / nums.size
  }

}
