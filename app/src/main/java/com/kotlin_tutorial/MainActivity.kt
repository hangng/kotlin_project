package com.kotlin_tutorial

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin_tutorial.adapter.FoodAdapter
import com.kotlin_tutorial.model.FoodItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), FoodAdapter.Listener {

    private lateinit var rvStudent: RecyclerView
    private var mFoodLst: ArrayList<FoodItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvStudent = findViewById(R.id.rv_food)
        val llMgr = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adpStudent = FoodAdapter(this, mFoodLst, this)
        rvStudent.setHasFixedSize(true)
        rvStudent.adapter = adpStudent
        rvStudent.layoutManager = llMgr
        retrieveFoodList()
        adpStudent.notifyDataSetChanged()
    }

    override fun onClick(iPosition: Int) {
        Log.i("TAG", "checking iPosition = " + iPosition)
    }

    fun retrieveFoodList() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("food_list").document("document_id")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val foodList = document.get("foods") as? List<Map<String, Any>>
                    if (foodList != null) {
                        // Iterate over the list of food items and do something with them
                        for (food in foodList) {
                            val category = food["category"] as? Long
                            val foodName = food["food_name"] as? String
                            val foodDesc = food["food_desc"] as? String
                            val foodCode = food["food_code"] as? String
                            val photo = food["food_photo"] as? String
                            val favorite = food["food_fav"] as? Boolean
                            // Do something with the fields of the food item
                            mFoodLst.add(
                                FoodItem(
                                    category!!.toInt(),
                                    foodName.toString(),
                                    foodDesc.toString(),
                                    foodCode.toString(),
                                    photo.toString(),
                                    favorite!!
                                )
                            )
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        Log.i("TAG", "checking mFoodLst size  = " + mFoodLst.size)
    }

    fun initFirebase() {
        val db = FirebaseFirestore.getInstance()

        val foodList: MutableList<Map<String, Any>> = mutableListOf()
        for (food in mFoodLst) {
            val foodMap: MutableMap<String, Any> = HashMap()
            foodMap["category"] = food.mCategory
            foodMap["food_name"] = food.mFoodName
            foodMap["food_desc"] = food.mFoodDesc
            foodMap["food_code"] = food.mFoodCode
            foodMap["food_photo"] = food.photo
            foodMap["food_fav"] = food.favorite
            foodList.add(foodMap)
        }

        db.collection("food_list")
            .document("document_id") // Replace with the ID of the document you want to overwrite, or remove this line to create a new document
            .set(mapOf("foods" to foodList))
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }
}