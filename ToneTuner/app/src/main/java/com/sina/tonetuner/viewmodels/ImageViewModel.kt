package com.sina.tonetuner.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel

class ImageViewModel: ViewModel() {

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPixelColor(bitmap: Bitmap, x: Int, y: Int): Int {
        return bitmap.getPixel(x, y)
    }

    fun colorToHex(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

}