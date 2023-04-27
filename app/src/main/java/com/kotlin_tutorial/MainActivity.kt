package com.kotlin_tutorial

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
    private var dataHelper: RecipeDataHelper? = null

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putSerializable(
            MainActivity.DATA_HELPER,
            dataHelper
        )
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bundle: Bundle? = null

        if (savedInstanceState != null) {
            bundle = savedInstanceState
        } else {
            bundle = intent.extras
        }

        if (bundle != null) {
            dataHelper = bundle.get(DATA_HELPER) as RecipeDataHelper
        }

        if (dataHelper == null) {
            dataHelper = RecipeDataHelper()
        }


        btnAdd = findViewById(R.id.btn_add)
        rvStudent = findViewById(R.id.rv_food)
        val llMgr = LinearLayoutManager(this)
//        val llMgr = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adpStudent = FoodAdapter(this, dataHelper!!.aryFoodLst, this, dataHelper!!)
        rvStudent.setHasFixedSize(true)
        rvStudent.adapter = adpStudent
        rvStudent.layoutManager = llMgr

        retrieveFoodList(adpStudent)
        btnAdd.setOnClickListener() {
            val viewIntent = Intent(this, CreateItem::class.java)
            startActivity(viewIntent)
        }
    }


    override fun onClick(iPosition: Int) {
        Log.i("TAG", "checking iPosition = " + iPosition)

    }

    fun retrieveFoodList(adpStudent: FoodAdapter) {
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe")
        docRef.get()
            .addOnSuccessListener { document ->
                dataHelper!!.aryFoodLst.add(
                    FoodItem(
                        "",
                        1, "",
                        "",
                        "",
                        FoodItem.food_category
                    )
                )

                for (doc in document) {
                    docRef.document(doc.id).get().addOnSuccessListener() { documentSnapshot ->
                        val data = documentSnapshot

                        if (!dataHelper!!.aryFoodCatTitle.contains(doc.id)) {
                            dataHelper!!.aryFoodCatTitle.add(doc.id)
                        }
                        dataHelper!!.aryFoodLst.add(
                            FoodItem(
                                foodCategoryName = documentSnapshot.getString(
                                    "foodCategory"
                                ).toString(),
//                                foodCategoryPosition = documentSnapshot.getString("catPosition")!!
//                                    .toLong().toInt(),
                                1,
                                foodName = documentSnapshot.getString(
                                    "foodName"
                                ).toString(),
                                foodDesc = documentSnapshot.getString(
                                    "foodName"
                                ).toString(),
                                photo = documentSnapshot.getString(
                                    "foodName"
                                ).toString(),
                                FoodItem.food_list
                            )
                        )
                        Log.d(TAG, "Document data: $data")
                        Log.d(TAG, "checkign aryFoodLst.size =  " + dataHelper!!.aryFoodLst.size)
                        adpStudent.notifyDataSetChanged()
                    }
                }


//                } else {
//                    Log.d(TAG, "No such document")
//                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }


//    fun initFirebase() {
//        val db = FirebaseFirestore.getInstance()
//
//        val foodList: MutableList<Map<String, Any>> = mutableListOf()
//        for (food in mFoodLst) {
//            val foodMap: MutableMap<String, Any> = HashMap()
//            foodMap["category"] = food.mCategory
//            foodMap["food_name"] = food.mFoodName
//            foodMap["food_desc"] = food.mFoodDesc
//            foodMap["food_code"] = food.mFoodCode
//            foodMap["food_photo"] = food.photo
//            foodMap["food_fav"] = food.favorite
//            foodList.add(foodMap)
//        }
//
//        db.collection("food_list")
//            .document("document_id") // Replace with the ID of the document you want to overwrite, or remove this line to create a new document
//            .set(mapOf("foods" to foodList))
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully written!")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error writing document", e)
//            }
    }

}