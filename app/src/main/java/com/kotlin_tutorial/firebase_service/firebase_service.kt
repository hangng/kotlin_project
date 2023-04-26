package com.kotlin_tutorial.firebase_service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseService {

    val TAG: String = "TAG"

    fun initialFirebase() {
        val db = FirebaseFirestore.getInstance()
    }

    fun dataCreate() {

    }

    fun dataRead(data: FirebaseFirestore): FirebaseFirestore {
        val docRef = data.collection("cities").document("LA")

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.i(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return data
    }

    fun dataUpdate() {

    }
}