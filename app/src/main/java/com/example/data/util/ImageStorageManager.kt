package com.example.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageManager {

    private const val DIRECTORY_NAME = "menu_images"
    private const val LOGO_DIRECTORY_NAME = "resto_logo"
    private const val MAX_DIMENSION = 800
    private const val COMPRESSION_QUALITY = 85

    /**
     * Copies, resizes, and saves a restaurant logo image into internal app storage
     * (`context.filesDir/resto_logo/logo_<timestamp>.png`).
     * Returns the absolute file path, or null on failure.
     */
    suspend fun saveLogoImageFromUri(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, LOGO_DIRECTORY_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return@withContext null

            // Resize maintaining aspect ratio (max dimension 800)
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > height) {
                if (width > MAX_DIMENSION) MAX_DIMENSION.toFloat() / width else 1f
            } else {
                if (height > MAX_DIMENSION) MAX_DIMENSION.toFloat() / height else 1f
            }

            val scaledBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val filename = "logo_${System.currentTimeMillis()}.png"
            val targetFile = File(dir, filename)
            val outputStream = FileOutputStream(targetFile)
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            originalBitmap.recycle()

            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Copies and compresses an image from a content Uri (Gallery/Camera) into
     * internal app storage (`context.filesDir/menu_images/menu_<uuid>.jpg`).
     * Returns the absolute path of the saved file, or null on failure.
     */
    suspend fun saveImageFromUri(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, DIRECTORY_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return@withContext null

            // Resize maintaining aspect ratio
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > height) {
                if (width > MAX_DIMENSION) MAX_DIMENSION.toFloat() / width else 1f
            } else {
                if (height > MAX_DIMENSION) MAX_DIMENSION.toFloat() / height else 1f
            }

            val scaledBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val filename = "menu_${UUID.randomUUID()}.jpg"
            val targetFile = File(dir, filename)
            val outputStream = FileOutputStream(targetFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()

            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            originalBitmap.recycle()

            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a stored image file from the internal directory.
     */
    suspend fun deleteImageFile(path: String?): Boolean = withContext(Dispatchers.IO) {
        if (path.isNullOrBlank()) return@withContext false
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Returns all images stored in the directory for backup purposes.
     */
    fun getImageDirectory(context: Context): File {
        val dir = File(context.filesDir, DIRECTORY_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}
