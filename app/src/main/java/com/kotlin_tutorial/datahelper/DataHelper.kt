package com.kotlin_tutorial.datahelper

import androidx.lifecycle.ViewModel
import com.kotlin_tutorial.model.FoodItem
import java.io.Serializable

class RecipeDataHelper {

    var sFoodCatTitle: String = ""
    var sTitle: String = ""
    var sPhotoName: String = ""
    var sSteps: String = ""
    var iCatPosition: Int = 0
    var aryCategory: ArrayList<String> = ArrayList()
    var aryFoodCatTitle: ArrayList<String> = ArrayList()
    var aryFoodLst: ArrayList<FoodItem> = ArrayList()
    var bEdit: Boolean = false
    var sDocumentId: String = ""


    constructor()

    constructor(
        sFoodCatTitle: String,
        sTitle: String,
        sPhotoName: String,
        sSteps: String,
        iCatPosition: Int,
        aryCategory: ArrayList<String>,
        aryFoodCatTitle: ArrayList<String>,
        aryFoodLst: ArrayList<FoodItem>,
        bEdit: Boolean,
        sDocumentId: String
    ) {
        this.sFoodCatTitle = sFoodCatTitle
        this.sTitle = sTitle
        this.sPhotoName = sPhotoName
        this.sSteps = sSteps
        this.iCatPosition = iCatPosition
        this.aryCategory = aryCategory
        this.aryFoodCatTitle = aryFoodCatTitle
        this.aryFoodLst = aryFoodLst
        this.bEdit = bEdit
        this.sDocumentId = sDocumentId
    }

}
