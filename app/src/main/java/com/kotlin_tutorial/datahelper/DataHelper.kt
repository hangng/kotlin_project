package com.kotlin_tutorial.datahelper

import com.kotlin_tutorial.model.FoodItem
import java.io.Serializable

class RecipeDataHelper : Serializable {
    companion object {
        private const val serialVersionUID: Long = -2009724719778639944L
    }


    var sFoodCatTitle: String = ""
        get() = field
        set(value) {
            field = value
        }
    var sTitle: String = ""
        get() = field
        set(value) {
            field = value
        }


    var sPhotoName: String = ""
        get() = field
        set(value) {
            field = value
        }

    var sSteps: String = ""
        get() = field
        set(value) {
            field = value
        }


    var iCatPosition: Int = 0
        get() = field
        set(value) {
            field = value
        }

    var aryCategory: ArrayList<String> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    var aryFoodCatTitle: ArrayList<String> = ArrayList()
        get() = field
        set(value) {
            field = value
        }
    var aryFoodLst: ArrayList<FoodItem> = ArrayList()
        get() = field
        set(value) {
            field = value
        }


}
