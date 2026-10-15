package com.skypass.skytrack.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Utility for compressing and resizing profile pictures before storing them in the database.
 * Compresses images into tiny (~15-25 KB) base64 JPEG data URIs so they take minimal database storage.
 */
object ImageCompressor {

    private const val DEFAULT_TARGET_SIZE = 256 // 256x256 px is optimal for mobile avatars (128dp @ 2x)
    private const val DEFAULT_JPEG_QUALITY = 75 // 75% quality gives great visual clarity at ~15-20 KB size

    /**
     * Center-crops a bitmap to a 1:1 square and scales it to [targetSize] x [targetSize].
     */
    fun cropAndResizeToSquare(source: Bitmap, targetSize: Int = DEFAULT_TARGET_SIZE): Bitmap {
        val width = source.width
        val height = source.height
        val minDimension = minOf(width, height)
        val cropX = (width - minDimension) / 2
        val cropY = (height - minDimension) / 2

        val cropped = Bitmap.createBitmap(source, cropX, cropY, minDimension, minDimension)
        return if (minDimension != targetSize) {
            Bitmap.createScaledBitmap(cropped, targetSize, targetSize, true)
        } else {
            cropped
        }
    }

    /**
     * Compresses the [bitmap] into a lightweight base64 Data URI string.
     * Output format: "data:image/jpeg;base64,..."
     */
    fun compressBitmapToBase64(
        bitmap: Bitmap,
        quality: Int = DEFAULT_JPEG_QUALITY,
        targetSize: Int = DEFAULT_TARGET_SIZE
    ): String {
        val squareBitmap = cropAndResizeToSquare(bitmap, targetSize)
        val outputStream = ByteArrayOutputStream()
        squareBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64"
    }

    /**
     * Decodes a base64 Data URI or raw base64 string back into a [Bitmap].
     */
    fun decodeBase64ToBitmap(dataUri: String?): Bitmap? {
        if (dataUri.isNullOrBlank()) return null
        return try {
            val base64Data = if (dataUri.contains(",")) {
                dataUri.substringAfter(",")
            } else {
                dataUri
            }.trim()
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Safely reads an image [Uri] from content resolver, sub-sampling it to avoid OOM
     * when the user picks a huge 12MP-50MP camera photo from Gallery.
     */
    fun decodeSampledBitmapFromUri(
        context: Context,
        uri: Uri,
        reqSize: Int = 512
    ): Bitmap? {
        return try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            // Calculate inSampleSize
            var inSampleSize = 1
            if (options.outHeight > reqSize || options.outWidth > reqSize) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= reqSize && (halfWidth / inSampleSize) >= reqSize) {
                    inSampleSize *= 2
                }
            }

            // Decode bitmap with inSampleSize set
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (_: Exception) {
            null
        }
    }
}
