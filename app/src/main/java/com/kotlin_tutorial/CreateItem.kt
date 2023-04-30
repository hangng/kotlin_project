package com.kotlin_tutorial

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
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
import androidx.core.content.FileProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kotlin_tutorial.adapter.RotationTransformation
import com.kotlin_tutorial.components.GlobalTools
import com.kotlin_tutorial.datahelper.RecipeDataHelper
import com.kotlin_tutorial.model.FoodItem
import com.kotlin_tutorial.model.firebase_service.FirebaseService
import com.squareup.picasso.Picasso
import java.io.File


class CreateItem : AppCompatActivity(), View.OnClickListener {
    companion object {
        val STORE_URI = "store_uri"
    }

    private var isEdit: Boolean = false
    private lateinit var imgItem: ImageView
    private lateinit var etSteps: EditText
    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var spCategory: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var btnDelete: Button
    private lateinit var dataHelper: RecipeDataHelper
    private val gson = Gson()
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 0
    private var imgUri: Uri? = null
    private lateinit var storageRef: StorageReference
    var catTitle = ""
    var categoryPosition = 0
    private lateinit var adpCategory: ArrayAdapter<String>

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STORE_URI, imgUri)
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

            imgUri = savedInstanceState.getParcelable(STORE_URI) ?: Uri.EMPTY

        } else {
            val personJson = intent.getStringExtra(MainActivity.DATA_HELPER)
            dataHelper = gson.fromJson(personJson, RecipeDataHelper::class.java)
        }

        initComponent()
        initData()

        btnSubmit.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    fun initComponent() {
// use the person object as needed
        etCategory = findViewById(R.id.et_category)
        imgItem = findViewById(R.id.iv_item)
        etSteps = findViewById(R.id.et_steps)
        etTitle = findViewById(R.id.et_food_name)
        spCategory = findViewById(R.id.sp_category)
        btnSubmit = findViewById(R.id.btn_submit)
        btnDelete = findViewById(R.id.btn_delete)

        storageRef = FirebaseStorage.getInstance().getReference(getString(R.string.image))
    }

    fun initData() {
        isEdit = dataHelper.bEdit
        etTitle.setText(dataHelper.sTitle)
        etSteps.setText(dataHelper.sSteps)

        if (dataHelper.aryFoodCatTitle.size < 1) {
            btnDelete.visibility = View.GONE
        } else if (dataHelper.bEdit) {
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }
        imgItem.setOnClickListener() {
            showDialog(
                getString(R.string.camera),
                getString(R.string.gallery),
                getString(R.string.cancel),
                getString(R.string.select_an_option)
            )
        }

        if (dataHelper.aryFoodCatTitle.size <= 1) {
            spCategory.visibility = View.GONE
        } else {
            spCategory.visibility = View.VISIBLE
        }

        dataHelper.aryFoodCatTitle.removeAt(0)
        adpCategory = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dataHelper.aryFoodCatTitle
        )
        spCategory.adapter = adpCategory

        for (i in 0 until dataHelper.aryFoodCatTitle.size) {
            if (dataHelper.sFoodCatTitle == dataHelper.aryFoodCatTitle[i]) {
                spCategory.setSelection(i)
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
                    // Handle the selected item
                    dataHelper.sFoodCatTitle = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing

                }
            }

        if (!dataHelper.sPhoto.isEmpty()) {

            Picasso.get()
                .load(dataHelper.sPhoto)
                .transform(RotationTransformation(90f))
                .into(imgItem)
            imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
            imgItem.layoutParams.height = 1000
            imgItem.layoutParams.width = 1000

            try {
                imgUri = Uri.parse(dataHelper.sPhoto)
            } catch (e: IllegalArgumentException) {
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imgUri = data.data!!
            FirebaseService().uploadFirebaseCloudStorage(this,imgUri) { imgUrl ->
                dataHelper.sPhoto = imgUrl
                screenOrientation(true)
                if (!dataHelper.sPhoto.isEmpty()) {
                    Picasso.get()
                        .load(dataHelper.sPhoto)
                        .transform(RotationTransformation(90f))
                        .into(imgItem)
                    imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
                    imgItem.layoutParams.height = 1000
                    imgItem.layoutParams.width = 1000
                }
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            FirebaseService().uploadFirebaseCloudStorage(this,imgUri) { imgUrl ->
                dataHelper.sPhoto = imgUrl
                screenOrientation(true)
                if (!dataHelper.sPhoto.isEmpty()) {
                    Picasso.get()
                        .load(dataHelper.sPhoto)
                        .transform(RotationTransformation(90f))
                        .into(imgItem)
                    imgItem.scaleType = ImageView.ScaleType.CENTER_CROP
                    imgItem.layoutParams.height = 1000
                    imgItem.layoutParams.width = 1000
                }
            }
        }
    }


    private fun showDialog(positive: String, negative: String, neutral: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)

        if (!positive.isEmpty()) {
            builder.setPositiveButton(positive) { dialog, which ->
                screenOrientation(false)
                takePhoto()
            }
        }

        if (!negative.isEmpty()) {
            builder.setNegativeButton(negative) { dialog, which ->
                screenOrientation(false)
                pickImage()
            }
        }

        if (!neutral.isEmpty()) {
            builder.setNeutralButton(neutral) { dialog, which ->
                dialog.dismiss()
            }
        }
        var dialog = builder.create()
        dialog.show()

    }

    private fun takePhoto() {
        var fileName = "photo"
        var storageDirec: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        try {
            var imgFile: File = File.createTempFile(fileName, ".jpg", storageDirec)
            dataHelper.photoPath = imgFile.absolutePath
            imgUri = FileProvider.getUriForFile(this, getString(R.string.provider_path), imgFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

        } catch (e: Exception) {

        }
    }
    override fun onClick(v: View) {
        if (v == btnSubmit) {
            submit()
        } else if (v == btnDelete) {
            FirebaseService().onDelete(this,dataHelper.sDocumentId) {
                GlobalTools.showSnackbar(this, R.string.delete_successful)
                finish()
            }
        }
    }


    fun submit() {
        dataHelper.sTitle = etTitle.text.toString()
        if (dataHelper.aryFoodCatTitle.size <= 0) {

            catTitle = etCategory.text.toString()
        } else if (!etCategory.text.toString().isEmpty()) {
            catTitle = etCategory.text.toString()
            dataHelper.aryFoodCatTitle.add(catTitle)
            categoryPosition = dataHelper.aryFoodCatTitle.size - 1
        } else {
            catTitle = spCategory.selectedItem.toString()
            categoryPosition = spCategory.selectedItemPosition
        }

        dataHelper.sSteps = etSteps.text.toString()
        if (dataHelper.sPhoto.isEmpty()) {
            dataHelper.sPhoto = "non"
        }
        if (dataHelper.bEdit) {
            val data = hashMapOf(
                "title" to dataHelper.sTitle as Any,
                "catPosition" to categoryPosition.toString() as Any,
                "foodCatTitle" to catTitle as Any,
                "steps" to dataHelper.sSteps as Any,
                "imgUrl" to dataHelper.sPhoto as Any,
            )
            FirebaseService().onUpdate(this,dataHelper.sDocumentId, data) {
                finish()
            }
        } else {

            // increase list index
            dataHelper.aryFoodLst.add(
                FoodItem(
                    catTitle,
                    dataHelper.sTitle,
                    dataHelper.sSteps,
                    dataHelper.sPhoto,
                    ""
                )
            )
            var documentIdIndex = "item_" + dataHelper.aryFoodLst.size
            val newData = hashMapOf(
                "title" to dataHelper.sTitle,
                "steps" to dataHelper.sSteps,
                "foodCatTitle" to catTitle,
                "imgUrl" to dataHelper.sPhoto,
            )
            FirebaseService().onAdd(this,documentIdIndex, newData) {
                GlobalTools.showSnackbar(this, R.string.add_successful)
                finish()
            }
        }

    }

    private fun screenOrientation(restoreDefault: Boolean) {
        if (restoreDefault) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}