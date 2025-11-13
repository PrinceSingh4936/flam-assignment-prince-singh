package com.princesingh.flam

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object SaveHelper {
    /**
     * Save raw RGBA byte buffer to PNG in Pictures/Flam.
     * - rgba: ByteArray with layout RGBA, length == width * height * 4
     * - returns absolute path on success, or null on failure
     */
    fun saveRgbaToPng(rgba: ByteArray, width: Int, height: Int): String? {
        return try {
            val bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(rgba))

            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Flam")
            if (!dir.exists()) dir.mkdirs()

            val name = "flam_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.png"
            val file = File(dir, name)
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
