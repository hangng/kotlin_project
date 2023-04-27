package com.kotlin_tutorial.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin_tutorial.R
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import com.kotlin_tutorial.model.FoodItem

class FoodAdapter(
    val context: Context,
    val mFoodItemList: List<FoodItem>,
    val mCallback: Listener,
    val mDataHelper: RecipeDataHelper
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View

        when (viewType) {
            FoodItem.food_category -> {
                v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
                return VhCategory(v)
            }
            FoodItem.food_list -> {
                v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_food, parent, false)
                return VhItems(v)
            }
            else -> {
                v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
                return VhCategory(v)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VhItems -> {
                val food = mFoodItemList[position]
//                holder.mCvItem.setCardBackgroundColor(
//                    ContextCompat.getColor(context, R.color.purple_200)
//                )
                holder.mTvFoodName.text = food.foodName
                holder.mLlRow.setOnClickListener {
                    mCallback.onClick(holder.adapterPosition)
//                    if (holder.mTvName.visibility == View.VISIBLE) {
//                        holder.mTvName.visibility = View.GONE
//                    } else {
//                        holder.mTvName.visibility = View.VISIBLE
//                    }
                }
            }
            is VhCategory -> {
                val foodCategory = mFoodItemList[position]
                holder.mTvFoodCat.text = foodCategory.foodCategoryName
                Log.i(
                    "TAG",
                    "checking foodCategory.foodCategoryName size = " + mDataHelper.aryFoodCatTitle.size
                )
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    mDataHelper.aryFoodCatTitle
                )
                holder.spCategory.adapter = adapter

                holder.spCategory.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Handle the selected item
                            val selectedItem = parent.getItemAtPosition(position).toString()
                            Log.i("TAG", "checking selectedItem = " + selectedItem)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do nothing
                        }
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return mFoodItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mFoodItemList[position].sectionId!!
    }

    inner class VhCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvFoodCat: TextView = itemView.findViewById(R.id.tv_cat)
        val spCategory: Spinner = itemView.findViewById(R.id.sp_category)
    }

    inner class VhItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val mTvSteps: TextView = itemView.findViewById(R.id.tv_steps)
        val mTvIngredients: TextView = itemView.findViewById(R.id.tv_ingredients)
        val mLlSteps: LinearLayout = itemView.findViewById(R.id.ll_steps)
        val mLlRow: LinearLayout = itemView.findViewById(R.id.ll_row)
        val mCvItem: CardView = itemView.findViewById(R.id.cv_items)
    }

    interface Listener {
        fun onClick(iPosition: Int)
    }
}



