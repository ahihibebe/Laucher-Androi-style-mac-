package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File

object FileStorageHelper {

    fun saveUriToInternalStorage(context: Context, uri: Uri, prefix: String): String? {
        return try {
            // Xóa toàn bộ file cũ có tiền tố tương ứng để tránh rác và không bị cache Coil giữ lại ảnh cũ
            cleanOldFiles(context, prefix)

            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
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

    fun cleanOldFiles(context: Context, prefix: String) {
        try {
            val dir = context.filesDir
            val files = dir.listFiles() ?: return
            for (f in files) {
                if (f.name.startsWith(prefix)) {
                    f.delete()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun deleteInternalFile(context: Context, prefix: String) {
        cleanOldFiles(context, prefix)
    }
}
