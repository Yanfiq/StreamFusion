package com.yanfiq.streamfusion.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.RawRes
import com.yanfiq.streamfusion.R
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


fun loadImageFromRaw(context: Context, @RawRes resId: Int): Bitmap {
    val inputStream = context.resources.openRawResource(resId)
//    val options = BitmapFactory.Options().apply {
//        inPreferredConfig = Bitmap.Config.ARGB_8888
//    }
//    return BitmapFactory.decodeResource(context.resources, resId, options)
    return BitmapFactory.decodeStream(inputStream)
}

fun loadImageFromDrawable(context: Context, resId: Int): Bitmap?{
    return BitmapFactory.decodeResource(context.resources, resId)
}

fun loadImageFromAssets(context: Context, fileName: String): Bitmap? {
    return try {
        val assetManager = context.resources.assets
        val inputStream = assetManager.open(fileName)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}