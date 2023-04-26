package com.kotlin_tutorial

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin_tutorial.adapter.StudentAdapter
import com.kotlin_tutorial.model.Student


class MainActivity : AppCompatActivity(), StudentAdapter.Listener {

    private lateinit var rvStudent: RecyclerView
    private var mAryList: ArrayList<Student> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mAryList.add(Student("John", 1, "male", "none",false))
        mAryList.add(Student("Mandy", 2, "Female", "none",false))
        mAryList.add(Student("George", 3, "male", "none",false))
        mAryList.add(Student("Joke", 4, "male", "none",false))


        rvStudent = findViewById(R.id.rv_student)
        val llMgr = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL,)
        val adpStudent = StudentAdapter(this,mAryList, this)
        rvStudent.setHasFixedSize(true)
        rvStudent.adapter = adpStudent
        rvStudent.layoutManager = llMgr
        adpStudent.notifyDataSetChanged()
        initFirebase()

    }

    override fun onClick(iPosition: Int) {
        Log.i("TAG", "checking iPosition = " + iPosition)
    }


    fun initFirebase() {
        val db = FirebaseFirestore.getInstance()

        // Create a new user with a first and last name
        // Create a new user with a first and last name
        val user: MutableMap<String, Any> = HashMap()
        user["first"] = "Ada"
        user["last"] = "Lovelace"
        user["born"] = 1815

// Add a new document with a generated ID

// Add a new document with a generated ID
        db.collection("kotlin_testing")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }


        db.collection("kotlin_testing")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "checking Error getting documents.", exception)
            }
    }
}