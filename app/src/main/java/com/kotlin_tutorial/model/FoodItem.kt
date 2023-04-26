package com.kotlin_tutorial.model

class FoodItem(

    val mCategory: Int,
    val mFoodCode: String,
    val mFoodName: String,
    val mFoodDesc: String,
    val photo: String,
    val favorite: Boolean
) {

    companion object{
        val FOOD_CAT_FAST_FOOD: Int = 0
        val FOOD_CAT_BREAKFAST_FOOD: Int = 1
        val FOOD_CAT_LUNCH_FOOD: Int = 2
        val FOOD_CAT_DINNER_FOOD: Int = 3
        val FOOD_CAT_DESSERT_FOOD: Int = 4

    }

}
