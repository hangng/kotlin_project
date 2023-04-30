package com.kotlin_tutorial.datahelper

import android.graphics.Bitmap
import android.net.Uri
import com.kotlin_tutorial.model.FoodItem

class RecipeDataHelper {

    var sFoodCatTitle: String = ""
    var sTitle: String = ""
    var sPhoto: String = ""
    var sSteps: String = ""
    var iRemovePosition: Int = 0
    var aryFoodCatTitle: ArrayList<String> = ArrayList()
    var aryFoodLst: ArrayList<FoodItem> = ArrayList()
    var aryFoodFullLst: ArrayList<FoodItem> = ArrayList()
    var bEdit: Boolean = false
    var sDocumentId: String = ""
    var photoBitmap: Bitmap? = null
    var photoPath: String = ""
    constructor()

}
