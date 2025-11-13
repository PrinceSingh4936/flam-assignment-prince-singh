package com.princesingh.flam

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageSaver {
    /**
     * rgba: ByteArray with width*height*4 bytes, RGBA order
     */
    fun saveRgbaToPng(context: Context, rgba: ByteArray, width: Int, height: Int, filename: String = "processed_sample.png"): String? {
        return try {
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val intPixels = IntArray(width * height)
            var i = 0
            var p = 0
            while (i < rgba.size && p < intPixels.size) {
                val r = rgba[i].toInt() and 0xff; val g = rgba[i+1].toInt() and 0xff
                val b = rgba[i+2].toInt() and 0xff; val a = rgba[i+3].toInt() and 0xff
                intPixels[p] = (a shl 24) or (r shl 16) or (g shl 8) or b
                i += 4; p += 1
            }
            bmp.setPixels(intPixels, 0, width, 0, 0, width, height)

            // Save to app files dir (accessible from Android Device File Explorer in Android Studio)
            val outFile = File(context.filesDir, filename)
            val fos = FileOutputStream(outFile)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            outFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
