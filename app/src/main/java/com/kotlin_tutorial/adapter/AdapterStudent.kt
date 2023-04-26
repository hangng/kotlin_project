package com.kotlin_tutorial.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kotlin_tutorial.MainActivity
import com.kotlin_tutorial.R
import com.kotlin_tutorial.model.Student

class StudentAdapter( val context:Context,val mStudentList: List<Student>, val mCallback: Listener) :
    RecyclerView.Adapter<StudentAdapter.VhItems>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhItems {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return VhItems(itemView)
    }

    override fun onBindViewHolder(holder: VhItems, position: Int) {
        val students = mStudentList[position]

        holder.mCvItem.setCardBackgroundColor(context.resources.getColor(R.color.purple_200))
        holder.mTvName.text = students.mName
        holder.mTvAge.text = students.mAge.toString()
        holder.mTvGender.text = students.mGender
        holder.mLlRow.setOnClickListener() {
            mCallback.onClick(holder.adapterPosition)
            if(holder.mTvName.visibility == View.VISIBLE){
                holder.mTvName .visibility = View.GONE
            }else
                holder.mTvName .visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mStudentList.size
    }


    inner class VhItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvName: TextView
        val mTvAge: TextView
        val mTvGender: TextView
        val mLlRow: LinearLayout
        val mCvItem: CardView

        init {
            mTvName = itemView.findViewById(R.id.tv_name)
            mTvAge = itemView.findViewById(R.id.tv_age)
            mTvGender = itemView.findViewById(R.id.tv_gender)
            mLlRow = itemView.findViewById(R.id.ll_row)
            mCvItem = itemView.findViewById(R.id.cv_items)
        }
    }


    interface Listener {
        fun onClick(iPosition: Int)
    }
}




