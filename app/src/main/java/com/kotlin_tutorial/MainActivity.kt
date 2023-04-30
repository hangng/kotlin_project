package com.kotlin_tutorial

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kotlin_tutorial.adapter.FoodAdapter
import com.kotlin_tutorial.components.GlobalTools
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import com.kotlin_tutorial.model.firebase_service.FirebaseService
import com.kotlin_tutorial.model.FoodItem
import java.util.*


class MainActivity : AppCompatActivity(), FoodAdapter.Listener, View.OnClickListener {

    companion object {
        val DATA_HELPER = "datahelper"
    }

    private lateinit var rvStudent: RecyclerView
    private lateinit var llRecipe: LinearLayout
    private lateinit var btnAdd: Button
    private var mFoodLst: ArrayList<FoodItem> = arrayListOf()
    private var mFoodFullLst: ArrayList<FoodItem> = arrayListOf()
    private lateinit var spCategory: Spinner
    private lateinit var adpFood: FoodAdapter
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
        if (savedInstanceState != null) {
            val personJson = savedInstanceState.getString(DATA_HELPER)
            dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
            mFoodLst.addAll(dataHelper.aryFoodLst)
        } else {
            dataHelper = RecipeDataHelper()
        }
        initComponent()
    }

    private fun initComponent() {
        llRecipe = findViewById(R.id.ll_recipe)
        btnAdd = findViewById(R.id.btn_add)
        rvStudent = findViewById(R.id.rv_food)
        spCategory = findViewById(R.id.sp_category)

        adpSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dataHelper.aryFoodCatTitle
        )
        spCategory.adapter = adpSpinner


        adpFood = FoodAdapter(this, mFoodLst, this, dataHelper)
        val llMgr = LinearLayoutManager(this)
        rvStudent.adapter = adpFood
        rvStudent.layoutManager = llMgr

        btnAdd.setOnClickListener(this)
    }


    override fun onEdit(iPosition: Int) {
        val dataHelpers = RecipeDataHelper()
        val gson = Gson()
        val intent = Intent(this, CreateItem::class.java)
        var data = mFoodLst.get(iPosition)
        dataHelpers.sTitle = data.foodName
        dataHelpers.sDocumentId = mFoodLst.get(iPosition).documentId
        dataHelpers.sSteps = data.foodDesc
        dataHelpers.aryFoodCatTitle = dataHelper.aryFoodCatTitle
        dataHelpers.bEdit = true
        dataHelpers.sPhoto = data.photo
        dataHelpers.aryFoodLst = mFoodLst
        dataHelpers.sFoodCatTitle = data.foodCategoryName
        dataHelpers.iRemovePosition = iPosition
        val personJson = gson.toJson(dataHelpers)
        intent.putExtra(DATA_HELPER, personJson)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        processData()
    }

    private fun processData() {
        FirebaseService().retrieveFoodLists(this) { document ->
            GlobalTools.showSnackbar(this, R.string.receive_successful)
            mFoodFullLst.clear()
            mFoodLst.clear()
            dataHelper.aryFoodCatTitle.clear()
            if (document != null) {
                dataHelper.aryFoodCatTitle.add(getString(R.string.all))
                for (doc in document) {

                    val data = doc.data
                    val title = data["title"] as String
                    val description = data["steps"] as String
                    val imageUrl = data["imgUrl"] as String
                    val foodCatTitle = data["foodCatTitle"] as String

                    if (!dataHelper.aryFoodCatTitle.contains(foodCatTitle)) {
                        dataHelper.aryFoodCatTitle.add(foodCatTitle)
                    }

                    mFoodFullLst.add(
                        FoodItem(
                            foodCategoryName = foodCatTitle,
                            foodName = title,
                            foodDesc = description,
                            photo = imageUrl,
                            documentId = doc.id,
                        )
                    )

                    if (mFoodFullLst.size == document.size()) {
                        mFoodLst.addAll(mFoodFullLst)
                    }
                }

                if (dataHelper.aryFoodCatTitle.size <= 1) {
                    llRecipe.visibility = View.GONE
                } else {
                    llRecipe.visibility = View.VISIBLE
                }

                if (document.size() == 0) {
                    FirebaseService().clearFirebaseCloudStorage(this)
                }

                processSpinner()
                adpSpinner.notifyDataSetChanged()
                adpFood.notifyDataSetChanged()

            } else {
                // Handle error case
            }
        }
    }

    override fun onClick(v: View?) {
        if (v == btnAdd) {
            val dataHelpers = RecipeDataHelper()
            dataHelpers.bEdit = false
            val gson = Gson()
            dataHelpers.aryFoodCatTitle = dataHelper.aryFoodCatTitle
            dataHelpers.aryFoodLst = mFoodLst
            val personJson = gson.toJson(dataHelpers)
            val intent = Intent(this, CreateItem::class.java)
            intent.putExtra(DATA_HELPER, personJson)
            startActivity(intent)
        }
    }

    private fun processSpinner() {
        mFoodLst.clear()

        Log.i("TAG", "checking dataHelper.sFoodCatTitle = " + dataHelper.sFoodCatTitle)

        if (dataHelper.sFoodCatTitle == getString(R.string.all)) {

            mFoodLst.addAll(
                mFoodFullLst
            )
        } else {
            for (category: FoodItem in mFoodFullLst) {
                if (category.foodCategoryName == dataHelper.sFoodCatTitle) {
                    mFoodLst.add(
                        FoodItem(
                            category.foodCategoryName,
                            category.foodName,
                            category.foodDesc,
                            category.photo,
                            category.documentId
                        )
                    )
                }
            }
        }


        spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    dataHelper.sFoodCatTitle = parent.selectedItem.toString()
                    if (dataHelper.sFoodCatTitle ==getString(R.string.all)) {
                        mFoodLst.clear()
                        mFoodLst.addAll(
                            mFoodFullLst
                        )
                    } else {
                        mFoodLst.clear()
                        for (category: FoodItem in mFoodFullLst) {
                            if (category.foodCategoryName == dataHelper.sFoodCatTitle) {
                                mFoodLst.add(
                                    FoodItem(
                                        category.foodCategoryName,
                                        category.foodName,
                                        category.foodDesc,
                                        category.photo,
                                        category.documentId
                                    )
                                )
                            }
                        }
                    }
                    adpFood.notifyDataSetChanged()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }
}

