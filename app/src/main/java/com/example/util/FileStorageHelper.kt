package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File

object FileStorageHelper {

    fun saveUriToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
        return try {
            val destinationFile = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteInternalFile(context: Context, fileName: String) {
        try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
