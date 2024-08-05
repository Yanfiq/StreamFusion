package com.yanfiq.streamfusion.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.io.File

fun getHiddenMessage(bitmap: Bitmap?): String {
    if(bitmap != null){
        // Get image width and height
        val width = bitmap.width
        val height = bitmap.height
        Log.d("Steganography", "Image size = (${width}x${height})")

        // Extract the binary data
        var printLength = false
        val binarySecret = StringBuilder()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val a = (bitmap.getPixel(x, y) shr 24) and 0xff
                val r = (bitmap.getPixel(x, y) shr 16) and 0xff
                val g = (bitmap.getPixel(x, y) shr 8) and 0xff
                val b = bitmap.getPixel(x, y) and 0xff

                if (a == 0x01) {
                    if(binarySecret.length <= 24){
                        Log.d("Steganography", "Pixel value at ${x}x${y}: ($a, $r, $g, $b)|rgb value: ${bitmap.getPixel(x, y)}")
                    }
                    binarySecret.append((r shr 7) and 1)
                    binarySecret.append((g shr 7) and 1)
                    binarySecret.append((b shr 7) and 1)
                }
                if(binarySecret.length >= 32 && !printLength){
                    Log.d("Steganography", "Length binary: ${binarySecret.substring(0, 32)}")
                    printLength = true
                }
            }
        }

        // Extract the length of the secret message
        val secretLengthBinary = binarySecret.substring(0, 32)
        val secretLength = secretLengthBinary.toInt(2)

        // Extract the secret message
        val messageBinary = binarySecret.substring(32, 32 + secretLength * 8)
        val secretMessage = binaryToText(messageBinary)

        Log.d("Steganography", "Secret message: ${secretMessage}")

        return secretMessage
    }
    return ""
}