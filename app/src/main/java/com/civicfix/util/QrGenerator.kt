package com.civicfix.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QrGenerator {
    fun generate(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(EncodeHintType.MARGIN to 1)
            val bits  = QRCodeWriter().encode(
                content, BarcodeFormat.QR_CODE, size, size, hints
            )
            val pixels = IntArray(size * size) { i ->
                val x = i % size
                val y = i / size
                if (bits[x, y]) Color.parseColor("#0A1628") else Color.WHITE
            }
            val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            bmp.setPixels(pixels, 0, size, 0, 0, size, size)
            bmp
        } catch (e: Exception) { null }
    }
}
