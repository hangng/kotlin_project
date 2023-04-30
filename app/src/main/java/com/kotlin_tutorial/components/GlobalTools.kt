package com.kotlin_tutorial.components

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kotlin_tutorial.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class GlobalTools {
    companion object {
        fun showSnackbar(activity: Activity, message: Int, duration: Int = Snackbar.LENGTH_SHORT) {
            val rootView: View = activity.findViewById(android.R.id.content)
            val snackbar = Snackbar.make(rootView, message, duration)
            snackbar.show()
        }


        fun showLoadingDialog(context: Context): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setView(R.layout.loading_dialog)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
            return dialog
        }

    }
}