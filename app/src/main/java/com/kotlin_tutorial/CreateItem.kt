package com.kotlin_tutorial

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import com.kotlin_tutorial.model.FoodItem
import java.util.HashMap

class CreateItem : AppCompatActivity() {

    private var isEdit: Boolean = false
    private lateinit var imgItem: ImageView
    private lateinit var etSteps: EditText
    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var spCategory: Spinner
    private lateinit var btnSubmit: Button
    private var dataHelper: RecipeDataHelper? = null

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(
            MainActivity.SHARE_EDIT,
            isEdit
        )

        outState.putSerializable(
            MainActivity.DATA_HELPER,
            dataHelper
        )
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)
        var bundle: Bundle? = null

        if (savedInstanceState != null) {
            bundle = savedInstanceState
        } else {
            bundle = intent.extras
        }

        if (bundle != null) {
            isEdit = bundle.get(MainActivity.SHARE_EDIT) as Boolean
            dataHelper = bundle.get(MainActivity.DATA_HELPER) as RecipeDataHelper
        }

        if (dataHelper == null) {
            dataHelper = RecipeDataHelper()
        }

        etCategory = findViewById(R.id.et_category)
        imgItem = findViewById(R.id.iv_item)
        etSteps = findViewById(R.id.et_steps)
        etTitle = findViewById(R.id.et_food_name)
        spCategory = findViewById(R.id.sp_category)
        btnSubmit = findViewById(R.id.btn_submit)


        if (dataHelper!!.aryFoodCatTitle.size <= 0) {
            spCategory.visibility = View.GONE
            etCategory.visibility = View.VISIBLE

        } else {
            spCategory.visibility = View.VISIBLE
            etCategory.visibility = View.GONE
        }

        btnSubmit.setOnClickListener {
            if (etTitle.length() < 0) {
                etTitle.setError("Please enter food name")
                return@setOnClickListener
            } else {
                dataHelper!!.sTitle = etTitle.text.toString()
            }

            if (dataHelper!!.aryFoodCatTitle.size <= 0) {
                dataHelper!!.iCatPosition = 1
                dataHelper!!.sFoodCatTitle = etCategory.text.toString()
            } else {
                dataHelper!!.iCatPosition = spCategory.selectedItemPosition
                dataHelper!!.sFoodCatTitle = spCategory.selectedItemPosition.toString()
            }


            dataHelper!!.sSteps = etSteps.text.toString()

            if (isEdit) {
                onUpdate()
            } else {
                onAdd()
            }

        }
    }


    fun onUpdate() {
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe").document("foodRecipeItems")

        val data = hashMapOf(
            "title" to dataHelper!!.sTitle,
            "catPosition" to dataHelper!!.iCatPosition,
            "steps" to dataHelper!!.sSteps,
            "photoName" to dataHelper!!.sPhotoName,
            "foodCatTitle" to dataHelper!!.sFoodCatTitle,

            )

        docRef.set(data)
            .addOnSuccessListener {
                finish()
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    fun onAdd() {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("foodRecipe").document(dataHelper!!.sFoodCatTitle)

        val newData = hashMapOf(
            "title" to dataHelper!!.sTitle,
            "catPosition" to dataHelper!!.iCatPosition,
            "steps" to dataHelper!!.sSteps,
            "photoName" to dataHelper!!.sPhotoName,
            "foodCatTitle" to dataHelper!!.sFoodCatTitle,
        )

        documentRef.set(newData)
            .addOnSuccessListener {
                finish()
                Log.d("MyApp", "New fields added to document")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error adding new fields to document", exception)
            }

    }

}