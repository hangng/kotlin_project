package com.kotlin_tutorial

import android.R.string
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.kotlin_tutorial.adapter.FoodAdapter
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import com.kotlin_tutorial.model.FoodItem
import java.util.*


class MainActivity : AppCompatActivity(), FoodAdapter.Listener {

    companion object {
        val SHARE_EDIT = "Edit"
        val DATA_HELPER = "datahelper"
    }

    private lateinit var rvStudent: RecyclerView
    private lateinit var btnAdd: Button
    private var mFoodLst: ArrayList<FoodItem> = arrayListOf()
//    private lateinit var dataHelper: RecipeDataHelper

    private var dataHelper: RecipeDataHelper? = null
    private val gson = Gson()
    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (dataHelper != null) {
            outState.putString(DATA_HELPER, gson.toJson(dataHelper))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFoodLst.clear()
        if (savedInstanceState != null) {
            val personJson = savedInstanceState.getString(DATA_HELPER)

            dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
            mFoodLst.addAll(dataHelper!!.aryFoodLst)
        } else {
            dataHelper = RecipeDataHelper()
        }



        btnAdd = findViewById(R.id.btn_add)
        rvStudent = findViewById(R.id.rv_food)



        Log.d(TAG, "checking mFoodLst.size =  " + mFoodLst.size)
        btnAdd.setOnClickListener() {
            val dataHelper = RecipeDataHelper()
            dataHelper.bEdit = false
            val gson = Gson()
            val personJson = gson.toJson(dataHelper)

// set properties of dataHelper as needed
            val intent = Intent(this, CreateItem::class.java)
            intent.putExtra(DATA_HELPER, personJson)
            startActivity(intent)
        }
    }




    override fun onClick(iPosition: Int) {

        val dataHelpers = RecipeDataHelper()
        var data = mFoodLst.get(iPosition)
        dataHelpers.sTitle = data.foodName
        dataHelpers.sDocumentId = dataHelper!!.aryFoodCatTitle.get(iPosition)
        dataHelpers.sSteps = data.foodDesc
        dataHelpers.aryFoodCatTitle = dataHelper!!.aryFoodCatTitle
        dataHelpers.bEdit = true
        dataHelpers.iCatPosition = data.foodCategoryPosition
        val gson = Gson()
        val personJson = gson.toJson(dataHelpers)

        Log.d(
            TAG,
            "checking data.dataHelper!!.aryFoodCatTitle =  " + dataHelper!!.aryFoodCatTitle + " | dataHelper!!.aryFoodCatTitle desc  = " + dataHelper!!.aryFoodCatTitle.get(
                iPosition
            ) + " | iPosition  = " + iPosition
        )

        val intent = Intent(this, CreateItem::class.java)
        intent.putExtra(DATA_HELPER, personJson)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        Log.d(
            TAG, "checking onResume"
        )
        val llMgr = LinearLayoutManager(this)
//        val llMgr = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adpStudent = FoodAdapter(this, mFoodLst, this, dataHelper!!)
        rvStudent.setHasFixedSize(true)
        rvStudent.adapter = adpStudent
        rvStudent.layoutManager = llMgr
        retrieveFoodList(adpStudent)
    }

    fun retrieveFoodList(adpStudent: FoodAdapter) {
        mFoodLst.clear()
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe")
        docRef.get().addOnSuccessListener { document ->
            mFoodLst.add(
                FoodItem(
                    "", 1, "", "", "", FoodItem.food_category
                )
            )
            dataHelper!!.aryFoodCatTitle.add("Create New Category")
            for (doc in document) {
                docRef.document(doc.id).get().addOnSuccessListener() { documentSnapshot ->
                    val data = documentSnapshot.getString(
                        "foodCatTitle"
                    ).toString()

                    if (!dataHelper!!.aryFoodCatTitle.contains(data)) {
                        dataHelper!!.aryFoodCatTitle.add(data)
                    }
                    mFoodLst.add(
                        FoodItem(
                            foodCategoryName = documentSnapshot.getString(
                                "foodCatTitle"
                            ).toString(),
                            foodCategoryPosition = documentSnapshot.getString("catPosition")!!
                                .toInt(),

                            foodName = documentSnapshot.getString(
                                "title"
                            ).toString(),
                            foodDesc = documentSnapshot.getString(
                                "steps"
                            ).toString(),

                            photo = documentSnapshot.getString(
                                "foodName"
                            ).toString(),
                            FoodItem.food_list
                        )
                    )
                    adpStudent.notifyDataSetChanged()
                    Log.d(TAG, "checking retrieveFoodList mFoodLst.size =  " + mFoodLst.size)
                }
            }


//                } else {
//                    Log.d(TAG, "No such document")
//                }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }

}