package com.example.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.viewmodel.LauncherMusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AudioScanner {

    suspend fun scanLocalAudio(context: Context): List<LauncherMusicTrack> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<LauncherMusicTrack>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                val gradientPairs = listOf(
                    0xFFE11D48 to 0xFFFB7185,
                    0xFF8B5CF6 to 0xFFC084FC,
                    0xFFF59E0B to 0xFFFDE047,
                    0xFF0D9488 to 0xFF2DD4BF,
                    0xFF3B82F6 to 0xFF60A5FA,
                    0xFFEC4899 to 0xFFF472B6,
                    0xFF10B981 to 0xFF34D399,
                    0xFF6366F1 to 0xFF818CF8
                )
                var gradIdx = 0

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val rawTitle = cursor.getString(titleCol) ?: "Không tên"
                    val rawArtist = cursor.getString(artistCol) ?: "Nghệ sĩ không xác định"
                    val durationMs = cursor.getLong(durationCol)
                    val albumId = cursor.getLong(albumIdCol)

                    // Skip very short audio clips (e.g. ringtones/notifications < 15s)
                    if (durationMs < 15000) continue

                    val totalSec = (durationMs / 1000).toInt().coerceAtLeast(1)
                    val min = totalSec / 60
                    val sec = totalSec % 60
                    val durationText = String.format("%02d:%02d", min, sec)

                    val audioContentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    val cleanArtist = if (rawArtist.contains("unknown", ignoreCase = true) || rawArtist.isBlank()) {
                        "Nghệ sĩ thiết bị"
                    } else {
                        rawArtist
                    }

                    val (gradStart, gradEnd) = gradientPairs[gradIdx % gradientPairs.size]
                    gradIdx++

                    tracks.add(
                        LauncherMusicTrack(
                            title = rawTitle,
                            artist = cleanArtist,
                            durationText = durationText,
                            totalSeconds = totalSec,
                            coverGradientStart = gradStart,
                            coverGradientEnd = gradEnd,
                            audioUri = audioContentUri,
                            albumArtUri = albumArtUri
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tracks
    }
}
