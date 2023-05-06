package com.deadrudolph.common_utils.file_utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileManager {

    /**
     * @return file path
     * */
    fun saveBitmap(
        contextWrapper: ContextWrapper,
        fileName: String,
        bitmap: Bitmap
    ): String {
        val directory: File = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
        val path = File(directory, fileName)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return path.absolutePath
    }
}