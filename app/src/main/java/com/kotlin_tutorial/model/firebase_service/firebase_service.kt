package com.kotlin_tutorial.model.firebase_service

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kotlin_tutorial.components.GlobalTools


class FirebaseService {
    private val docRef = Firebase.firestore.collection("foodRecipe")
    private var storageRef: StorageReference = FirebaseStorage.getInstance().getReference("Images")

    fun retrieveFoodLists(context: Context, callback: (QuerySnapshot?) -> Unit) {
        val progressDialog = GlobalTools.showLoadingDialog(context)

        docRef.get().addOnSuccessListener { document ->
            callback(document)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error retrieving food list", exception)
            callback(null)
        }.addOnCompleteListener {
            if( progressDialog.isShowing){
                progressDialog.dismiss()
            }
            Log.d(TAG, "Food list retrieval completed")
        }
    }

    fun clearFirebaseCloudStorage(context:Context) {
        val storage = Firebase.storage
        val folderRef = storage.reference.child("Images")
        val progressDialog = GlobalTools.showLoadingDialog(context)

        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                // Delete each file in the folder
                listResult.items.forEach { item ->
                    item.delete()
                }
                // Delete the folder itself
                folderRef.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Folder deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting folder", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error listing folder contents", e)
            }.addOnCompleteListener{
                if( progressDialog.isShowing){
                    progressDialog.dismiss()
                }
            }
    }

    fun uploadFirebaseCloudStorage(context: Context,imgUri: Uri?, callback: (String) -> Unit) {
        var imgUrl = ""
        val imgId = FirebaseDatabase.getInstance().getReference("foodRecipe").push().key
        val progressDialog = GlobalTools.showLoadingDialog(context)


        imgUri?.let { //check if null
            storageRef.child(imgId!!)
                .putFile(imgUri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        imgUrl = url.toString()
                        Log.i("TAG", "checking upload success...")
                        callback(imgUrl) // call the callback function with the imgUrl
                    }

                }.addOnFailureListener { fail ->
                    imgUrl = ""
                    Log.i("TAG", "checking upload failed...")
                    callback(imgUrl) // call the callback function with the imgUrl
                }.addOnCompleteListener {
                    if( progressDialog.isShowing){
                        progressDialog.dismiss()
                    }
                }
        } ?: run {
            // Code to be executed if imgUri is null
            Log.i(TAG, "Image URI is null")
            callback("")
        }

    }


    fun onDelete(context: Context,documentId: String, callback: () -> Unit) {
        val progressDialog = GlobalTools.showLoadingDialog(context)

        docRef.document(documentId).delete()
            .addOnSuccessListener {
                callback()
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }.addOnCompleteListener{
                if( progressDialog.isShowing){
                    progressDialog.dismiss()
                }
            }
    }

    fun onAdd(context: Context,documentId: String, newData: HashMap<String, String>, callback: () -> Unit) {
        val progressDialog = GlobalTools.showLoadingDialog(context)

        docRef.document(documentId).set(newData)
            .addOnSuccessListener {
                callback()
                Log.d("MyApp", "New fields added to document")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error adding new fields to document", exception)
            }.addOnCompleteListener{
                if( progressDialog.isShowing){
                    progressDialog.dismiss()
                }
            }
    }

    fun onUpdate(context: Context,documentId: String, newData: HashMap<String, Any>, callback: () -> Unit) {
        val progressDialog = GlobalTools.showLoadingDialog(context)

        docRef.document(documentId).update(newData)
            .addOnSuccessListener {
                callback()
                Log.d("MyApp", "New fields added to document")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error adding new fields to document", exception)
            }.addOnCompleteListener{
                if( progressDialog.isShowing){
                    progressDialog.dismiss()
                }
            }
    }
}
