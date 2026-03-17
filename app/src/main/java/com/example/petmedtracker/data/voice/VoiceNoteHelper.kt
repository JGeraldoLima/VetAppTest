package com.example.petmedtracker.data.voice

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

/**
 * Handles recording, playback, and deletion of voice notes.
 * Uses app cache directory for temporary files.
 */
class VoiceNoteHelper(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordPath: String? = null
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Starts recording. Returns the path where the file will be saved, or null on failure.
     * Call stopRecording() to finish and get the path.
     */
    fun startRecording(): String? {
        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        currentRecordPath = file.absolutePath
        return try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            file.absolutePath
        } catch (e: IOException) {
            currentRecordPath = null
            null
        }
    }

    /**
     * Stops recording and returns the path to the saved file, or null if recording was not active.
     */
    fun stopRecording(): String? {
        val path = currentRecordPath
        currentRecordPath = null
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            path
        } catch (e: Exception) {
            null
        }
    }

    fun isRecording(): Boolean = mediaRecorder != null

    /**
     * Plays the audio file at the given path.
     * onCompletion is called when playback finishes.
     */
    fun play(path: String, onCompletion: () -> Unit = {}) {
        stopPlayback()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                    onCompletion()
                }
                start()
            }
        } catch (e: IOException) {
            onCompletion()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    /**
     * Deletes the file at the given path. Returns true if deleted or file did not exist.
     */
    fun deleteFile(path: String): Boolean {
        if (path.isEmpty()) return true
        return try {
            File(path).delete()
        } catch (e: Exception) {
            false
        }
    }
}
