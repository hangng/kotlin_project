package com.kotlin_tutorial.model

class FoodItem(

    val foodCategoryName: String,
    var foodCategoryPosition: Int,
    val foodName: String,
    val foodDesc: String,
    val photo: String,
    val documentId: String,
    val sectionId: Int?,
) {
    companion object {
        val food_category: Int = 0
        val food_list: Int = 1

    }

    override fun toString(): String {
        return foodCategoryName
    }
}
