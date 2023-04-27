package com.kotlin_tutorial

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
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
    private lateinit var dataHelper: RecipeDataHelper
    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        Log.i("TAG", "checking onSaveInstanceState")
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            MainActivity.SHARE_EDIT,
            isEdit
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        initComponent()
        initData()


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


            dataHelper.sSteps = etSteps.text.toString()

            var catTitle = ""
            if (dataHelper.aryFoodCatTitle.size <= 0) {
                catTitle = etCategory.text.toString()
            } else {
                catTitle = spCategory.getSelectedItem().toString();
            }

            if (isEdit) {
                onUpdate(catTitle)
            } else {
                onAdd(catTitle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun onUpdate(catTitle: String) {
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe").document(catTitle)

        val data = hashMapOf(
            "title" to dataHelper.sTitle,
            "catPosition" to dataHelper.iCatPosition.toString(),
            "steps" to dataHelper.sSteps,
            "photoName" to dataHelper.sPhotoName,
            "foodSteps" to dataHelper.sFoodCatTitle,
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

    fun onAdd(catTitle: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("foodRecipe").document()

        var position: Int = 0

        if (dataHelper.aryFoodCatTitle.size <= 0) {
            position = 0
        } else {
            position = dataHelper.iCatPosition
        }
        val newData = hashMapOf(
            "title" to dataHelper.sTitle,
            "catPosition" to position.toString(),
            "steps" to dataHelper.sSteps,
            "photoName" to dataHelper.sPhotoName,
            "foodCatTitle" to dataHelper.sFoodCatTitle,
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

    fun initComponent() {
// use the person object as needed
        etCategory = findViewById(R.id.et_category)
        imgItem = findViewById(R.id.iv_item)
        etSteps = findViewById(R.id.et_steps)
        etTitle = findViewById(R.id.et_food_name)
        spCategory = findViewById(R.id.sp_category)
        btnSubmit = findViewById(R.id.btn_submit)

    }

    fun initData() {

        val gson = Gson()
        val personJson = intent.getStringExtra(MainActivity.DATA_HELPER)
        dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
        isEdit = dataHelper.bEdit
        etTitle.setText(dataHelper.sTitle)
        etSteps.setText(dataHelper.sSteps)

        if (dataHelper.aryFoodCatTitle.size <= 0) {
            spCategory.visibility = View.GONE
            etCategory.visibility = View.VISIBLE

        } else {
            spCategory.visibility = View.VISIBLE
            etCategory.visibility = View.GONE
        }


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dataHelper.aryFoodCatTitle
        )
        spCategory.adapter = adapter
        spCategory.setSelection(dataHelper.iCatPosition)
        spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Handle the selected item
                    dataHelper.sFoodCatTitle = parent.selectedItem.toString()
                    Log.i("TAG", "checking dataHelper.sFoodCatTitle = " + dataHelper.sFoodCatTitle)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing

                }
            }
    }

}