package com.kotlin_tutorial.adapter

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
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
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class FoodAdapter(
    val context: Context,
    val mFoodItemList: List<FoodItem>,
    val mCallback: Listener,
    val mDataHelper: RecipeDataHelper
) : RecyclerView.Adapter<FoodAdapter.VhItems>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItems {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return VhItems(v)
    }

    override fun onBindViewHolder(holder: VhItems, position: Int) {
        val food = mFoodItemList[position]
        holder.mTvFoodName.text = food.foodName
        holder.mRlRow.setOnClickListener {
            mCallback.onEdit(holder.adapterPosition)
        }

        if (!food.photo.isEmpty()) {
            Picasso.get()
                .load(food.photo)
                .transform(RotationTransformation(90f))
                .into(holder.mIvItem)

            holder.mIvItem.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun getItemCount(): Int {
        return mFoodItemList.size
    }


    class VhItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val mRlRow: RelativeLayout = itemView.findViewById(R.id.rl_row)
        val mIvItem: ImageView = itemView.findViewById(R.id.iv_img)
    }

    interface Listener {
        fun onEdit(iPosition: Int)
    }
}
class RotationTransformation(private val angle: Float) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        val rotatedBitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        source.recycle()
        return rotatedBitmap
    }

    override fun key(): String {
        return "rotate$angle"
    }
}


