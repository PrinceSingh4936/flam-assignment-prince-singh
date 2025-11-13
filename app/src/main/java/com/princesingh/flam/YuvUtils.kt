package com.princesingh.flam

import android.media.Image
import java.nio.ByteBuffer

object YuvUtils {
    fun imageToNv21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]

        val ySize = yPlane.buffer.remaining()
        val uSize = uPlane.buffer.remaining()
        val vSize = vPlane.buffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yPlane.buffer.get(nv21, 0, ySize)

        // Read U and V
        val uBytes = ByteArray(uSize)
        val vBytes = ByteArray(vSize)
        uPlane.buffer.get(uBytes)
        vPlane.buffer.get(vBytes)

        // Interleave VU as NV21 expects V then U bytes for each chroma pair
        var pos = ySize
        val chroma = width / 2 * height / 2
        var i = 0
        while (i < vBytes.size && pos + 1 < nv21.size) {
            nv21[pos++] = vBytes[i]
            nv21[pos++] = uBytes[i]
            i++
        }
        return nv21
    }
}
