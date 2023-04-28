package com.kotlin_tutorial

import android.R.string
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
    private var mCategoryLst: ArrayList<String> = arrayListOf()
    private lateinit var spCategory: Spinner
    private lateinit var adpStudent: FoodAdapter
    private lateinit var adpSpinner: ArrayAdapter<String>


    private var dataHelper: RecipeDataHelper = RecipeDataHelper()
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
            mFoodLst.addAll(dataHelper.aryFoodLst)
        } else {
            dataHelper = RecipeDataHelper()
        }



        btnAdd = findViewById(R.id.btn_add)
        rvStudent = findViewById(R.id.rv_food)
        spCategory = findViewById(R.id.sp_category)

        adpSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dataHelper.aryFoodCatTitle
        )
        spCategory.adapter = adpSpinner

        adpStudent = FoodAdapter(this, mFoodLst, this, dataHelper)
        val llMgr = LinearLayoutManager(this)
        rvStudent.adapter = adpStudent
        rvStudent.layoutManager = llMgr

        btnAdd.setOnClickListener() {
            val dataHelper = RecipeDataHelper()
            dataHelper.bEdit = false
            val gson = Gson()
            dataHelper.aryFoodCatTitle = mCategoryLst
            val personJson = gson.toJson(dataHelper)
            Log.d(
                TAG,
                "checking mCategoryLst =  " + mCategoryLst.size
            )

            val intent = Intent(this, CreateItem::class.java)
            intent.putExtra(DATA_HELPER, personJson)
            startActivity(intent)
        }
    }


    override fun onClick(iPosition: Int) {
        val dataHelpers = RecipeDataHelper()
        var data = mFoodLst.get(iPosition)
        dataHelpers.sTitle = data.foodName
        dataHelpers.sDocumentId = mFoodLst.get(iPosition).documentId
        dataHelpers.sSteps = data.foodDesc
        dataHelpers.aryFoodCatTitle = dataHelper.aryFoodCatTitle
        dataHelpers.bEdit = true
        dataHelpers.iCatPosition = data.foodCategoryPosition
        val gson = Gson()
        val personJson = gson.toJson(dataHelpers)

        Log.d(
            TAG,
            "checking data.dataHelper!!.aryFoodCatTitle =  " + dataHelper.aryFoodCatTitle
        )

        val intent = Intent(this, CreateItem::class.java)
        intent.putExtra(DATA_HELPER, personJson)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        retrieveFoodList()
        adpStudent.notifyDataSetChanged()
    }

    fun retrieveFoodList() {
        mFoodLst.clear()
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe")
        docRef.get().addOnSuccessListener { document ->
            for (doc in document) {
                docRef.document(doc.id).get().addOnSuccessListener() { documentSnapshot ->
                    val data = documentSnapshot.getString(
                        "foodCatTitle"
                    ).toString()

                    if (!dataHelper.aryFoodCatTitle.contains(data)) {
                        dataHelper.aryFoodCatTitle.add(data)
                        mCategoryLst.add(data)

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
                            documentId = doc.id,
                            FoodItem.food_list
                        )
                    )

                    adpSpinner.notifyDataSetChanged()
                    adpStudent.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }

//    fun initFirebaseCloudStorage() {
//        val storage = FirebaseStorage.getInstance()
//        val storageRef = storage.reference
//        val fileUri = // URI of the file to upload
//
//        val fileRef = storageRef.child("images/${fileUri.lastPathSegment}")
//        val uploadTask = fileRef.putFile(fileUri)
//
//        uploadTask.addOnSuccessListener {
//            // File uploaded successfully
//        }.addOnFailureListener {
//            // Upload failed
//        }
//
//    }


}