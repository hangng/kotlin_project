package com.kotlin_tutorial

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeFile
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CreateItem : AppCompatActivity() {

    private var isEdit: Boolean = false
    private lateinit var imgItem: ImageView
    private lateinit var etSteps: EditText
    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var spCategory: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var dataHelper: RecipeDataHelper
    private val gson = Gson()
    private val PICK_IMAGE_REQUEST = 1

    private val REQUEST_IMAGE_CAPTURE = 0
    private var imgUri: Uri? = null
    private lateinit var storageRef: StorageReference


    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (dataHelper != null) {
            outState.putString(MainActivity.DATA_HELPER, gson.toJson(dataHelper))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        if (savedInstanceState != null) {
            val personJson = savedInstanceState.getString(MainActivity.DATA_HELPER)
            dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
        } else {
            val personJson = intent.getStringExtra(MainActivity.DATA_HELPER)
            dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
        }


        initComponent()
        initData()

        btnSubmit.setOnClickListener {

//            if (etTitle.length() < 0) {
//                etTitle.setError("Please enter food name")
//                return@setOnClickListener
//            } else {
//                dataHelper.sTitle = etTitle.text.toString()
//            }
//
//            if (dataHelper.aryFoodCatTitle.size <= 0) {
//                dataHelper.iCatPosition = 1
//                dataHelper.sFoodCatTitle = etCategory.text.toString()
//            } else {
//                dataHelper.iCatPosition = spCategory.selectedItemPosition
//                dataHelper.sFoodCatTitle = spCategory.selectedItemPosition.toString()
//            }
//
//
//            dataHelper.sSteps = etSteps.text.toString()
//
            var catTitle = ""
            if (dataHelper.aryFoodCatTitle.size <= 0) {
                catTitle = etCategory.text.toString()
            } else {
                catTitle = spCategory.selectedItemPosition.toString();
            }
            uploadImgFirebaseCloudStorage()
            Log.i("TAG", "checking    uploadImgFirebaseCloudStorage() = "+    uploadImgFirebaseCloudStorage())
//            if (isEdit) {
//                onUpdate(dataHelper.sDocumentId, catTitle)
//            } else {
//            onAdd(catTitle, uploadImgFirebaseCloudStorage())
//            }
        }
    }

    override fun onResume() {

        super.onResume()
    }

    fun onUpdate(documentId: String, catTitle: String) {
        val db = Firebase.firestore
        val docRef = db.collection("foodRecipe").document(documentId)

        Log.i(
            "TAG",
            "checking catTitle = " + catTitle + " | dataHelper.sSteps = " + dataHelper.sSteps + " | dataHelper.sTitle = " + dataHelper.sTitle
        )
        val data = hashMapOf(
            "title" to dataHelper.sTitle,
            "catPosition" to catTitle,
            "photoName" to dataHelper.sPhotoName,
            "steps" to dataHelper.sSteps,
        )

        docRef.set(data)
            .addOnSuccessListener {
                finish()
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    fun onAdd(catTitle: String, imgUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("foodRecipe").document()

        var position: Int = 0

        if (dataHelper.aryFoodCatTitle.size <= 0) {
            position = 0
        } else {
            position = dataHelper.iCatPosition
        }
        val newData = hashMapOf(
            "title" to dataHelper.sTitle,
            "catPosition" to position.toString(),
            "steps" to dataHelper.sSteps,
            "photoName" to dataHelper.sPhotoName,
            "foodCatTitle" to catTitle,
            "imgUrl" to imgUrl,
        )

        documentRef.set(newData)
            .addOnSuccessListener {
                finish()
                Log.d("MyApp", "New fields added to document")
            }
            .addOnFailureListener { exception ->
                Log.e("MyApp", "Error adding new fields to document", exception)
            }

    }

    fun initComponent() {
// use the person object as needed
        etCategory = findViewById(R.id.et_category)
        imgItem = findViewById(R.id.iv_item)
        etSteps = findViewById(R.id.et_steps)
        etTitle = findViewById(R.id.et_food_name)
        spCategory = findViewById(R.id.sp_category)
        btnSubmit = findViewById(R.id.btn_submit)

        storageRef = FirebaseStorage.getInstance().getReference("Images")

    }

    fun initData() {
        if (dataHelper.photoBitmap != null) {
            val matrix = Matrix()
            val rotatedBitmap =
                Bitmap.createBitmap(
                    dataHelper.photoBitmap!!,
                    0,
                    0,
                    dataHelper.photoBitmap!!.width,
                    dataHelper.photoBitmap!!.height,
                    matrix,
                    true
                )
            imgItem.setImageBitmap(rotatedBitmap)
            imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
            imgItem.layoutParams.height = 1000
            imgItem.layoutParams.width = 1000
        }

        isEdit = dataHelper.bEdit
        etTitle.setText(dataHelper.sTitle)
        etSteps.setText(dataHelper.sSteps)
        Log.i("TAG", "checking title = " + dataHelper.sTitle)

        Log.i("TAG", "checking documentId = " + dataHelper.sDocumentId)
        Log.i(
            "TAG",
            "checking dataHelper.aryFoodCatTitle.toString() = " + dataHelper.aryFoodCatTitle.toString()
        )
        Log.i("TAG", "checking sSteps = " + dataHelper.sSteps)

        if (dataHelper.aryFoodCatTitle.size <= 0) {
            spCategory.visibility = View.GONE
            etCategory.visibility = View.VISIBLE

        } else {
            spCategory.visibility = View.VISIBLE
            etCategory.visibility = View.GONE
        }

        imgItem.setOnClickListener() {
            showDialog()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dataHelper.aryFoodCatTitle
        )
        spCategory.adapter = adapter
        spCategory.setSelection(dataHelper.iCatPosition)
        spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Handle the selected item
                    dataHelper.sFoodCatTitle = parent.selectedItem.toString()
                    Log.i("TAG", "checking dataHelper.sFoodCatTitle = " + dataHelper.sFoodCatTitle)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing

                }
            }
    }

    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val matrix = Matrix()
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            var filePath: Uri = data.data!!
            imgItem.setImageURI(filePath)
            imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
            imgItem.layoutParams.height = 1000
            imgItem.layoutParams.width = 1000

            val inputStream = this.contentResolver.openInputStream(filePath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            dataHelper.photoBitmap = bitmap

            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            dataHelper.photoBitmap = rotatedBitmap
            imgItem.setImageURI(filePath)
            imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
            imgItem.layoutParams.height = 1000
            imgItem.layoutParams.width = 1000

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var bmp: Bitmap = decodeFile(dataHelper.photoPath)
            val exif = ExifInterface(dataHelper.photoPath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
            }
            val rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
            dataHelper.photoBitmap = rotatedBitmap
            imgItem.setImageBitmap(rotatedBitmap)
            imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
            imgItem.layoutParams.height = 1000
            imgItem.layoutParams.width = 1000
        }
    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Select and Option")
        builder.setPositiveButton("Camera") { dialog, which ->
            takePhoto()
        }
        builder.setNegativeButton("Gallery") { dialog, which ->
            pickImage()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun takePhoto() {
        var fileName = "photo"
        var storageDirec: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        try {
            var imgFile: File = File.createTempFile(fileName, ".jpg", storageDirec)
            dataHelper.photoPath = imgFile.absolutePath
            imgUri = FileProvider.getUriForFile(this, "com.kotlin_tutorial.fileprovider", imgFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

        } catch (e: Exception) {

        }
    }

    private fun uploadImgFirebaseCloudStorage(): String {
        var imgUrl: String = ""

        Log.i("TAG", "checking imgUri = " + imgUri)

        imgUri?.let {
            storageRef.child(dataHelper.sDocumentId)
                .putFile(imgUri!!) // if success stored img then generate url from Firebase
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        imgUrl = url.toString()

                        Log.i("TAG", "checking upload success...")
                    }

                }.addOnFailureListener { fail ->
                    imgUrl = ""
                    Log.i("TAG", "checking upload failed...")
                }
        }
        return imgUrl
    }


}