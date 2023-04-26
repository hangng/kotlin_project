package com.kotlin_tutorial.components

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class GlobalTools {


    fun BitMapToString(bitmap: Bitmap): String {
        var baos: ByteArrayOutputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        var b = baos.toByteArray()

        var pic: String = Base64.encodeToString(b, Base64.DEFAULT)
        return pic
    }


}