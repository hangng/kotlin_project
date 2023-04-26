package com.kotlin_tutorial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin_tutorial.R
import com.kotlin_tutorial.model.FoodItem

class FoodAdapter(
    val context: Context,
    val mFoodItemList: List<FoodItem>,
    val mCallback: Listener
) :
    RecyclerView.Adapter<FoodAdapter.VhItems>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItems {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return VhItems(itemView)
    }

    override fun onBindViewHolder(holder: VhItems, position: Int) {
        val food = mFoodItemList[position]

        holder.mCvItem.setCardBackgroundColor(context.resources.getColor(R.color.purple_200))
        holder.mTvFoodName.text = food.mFoodName
        holder.mTvFoodDesc.text = food.mFoodDesc
//        holder.mLlRow.setOnClickListener() {
//            mCallback.onClick(holder.adapterPosition)
//            if (holder.mTvName.visibility == View.VISIBLE) {
//                holder.mTvName.visibility = View.GONE
//            } else
//                holder.mTvName.visibility = View.VISIBLE
//        }
    }

    override fun getItemCount(): Int {
        return mFoodItemList.size
    }


    inner class VhItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvFoodName: TextView
        val mTvFoodDesc: TextView
        val mTvFoodFav: TextView
        val mTvFoodAdd: TextView
        val mLlRow: LinearLayout
        val mCvItem: CardView

        init {
            mTvFoodName = itemView.findViewById(R.id.tv_food_name)
            mTvFoodDesc = itemView.findViewById(R.id.tv_desc)
            mTvFoodFav = itemView.findViewById(R.id.tv_favorite)
            mTvFoodAdd = itemView.findViewById(R.id.tv_add)
            mLlRow = itemView.findViewById(R.id.ll_row)
            mCvItem = itemView.findViewById(R.id.cv_items)
        }
    }


    interface Listener {
        fun onClick(iPosition: Int)
    }
}




