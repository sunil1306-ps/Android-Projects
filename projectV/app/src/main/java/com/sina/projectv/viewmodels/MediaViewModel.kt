package com.sina.projectv.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.video.VideoSize


class MediaViewModel() : ViewModel() {

    private var exoPlayer: ExoPlayer? = null
    var isPlaying = mutableStateOf(false)
    var isVideoDataLoading by mutableStateOf(true)

    var videoTitle by mutableStateOf("Title")
    var videoDuration by mutableIntStateOf(100)
    var error by mutableStateOf("")


    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        15000, // Minimum buffer before playback starts
                        50000, // Maximum buffer to hold
                        3000, // Buffer required for playback to resume
                        5000 // Buffer required after re-buffer
                    )
                    .build()
            )
            .build()


        // Add Listeners for debugging
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> Log.d("ExoPlayer", "Buffering...")
                    Player.STATE_READY -> Log.d("ExoPlayer", "Playback ready")
                    Player.STATE_ENDED -> Log.d("ExoPlayer", "Playback ended")
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                // Optional: Synchronize playback manually if needed
                val currentPosition = exoPlayer?.currentPosition ?: 0
                val duration = exoPlayer?.duration ?: Long.MAX_VALUE
                if (currentPosition > duration) {
                    exoPlayer?.seekTo(currentPosition)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayer", "Playback error: ${error.message}")
            }
        })
    }

    fun loadMediaItem(context: Context, mediaUrl: String) {
        val mediaItem = MediaItem.fromUri(mediaUrl)

        val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            .createMediaSource(mediaItem)

        exoPlayer?.apply {
            setMediaSource(mediaSource)
            prepare()
        }
    }

    fun playMedia(context: Context) {
        exoPlayer?.play()
        isPlaying.value = true
    }

    fun pauseMedia() {
        exoPlayer?.pause()
        isPlaying.value = false
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        isPlaying.value = false
    }

    fun stopPlayer(context: Context) {
        exoPlayer?.stop()
        isPlaying.value = false
    }

    fun getPlayerState(): Int {
        return exoPlayer?.playbackState ?: Player.STATE_IDLE
    }

    fun getPlayer(): ExoPlayer? {
        return exoPlayer
    }

    fun setLandscapeOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    // Notify the UI to change back to portrait orientation
    fun setPortraitOrientation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

//    fun getVideoMetadata(videoUrl: String) {
//        val metadataRetriever = MediaMetadataRetriever()
//        val metadata = mutableMapOf<String, String>()
//
//        try {
//            // Set the video URL as the data source
//            metadataRetriever.setDataSource(videoUrl, HashMap())
//
//            // Extract metadata
//            metadata["Title"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown"
//            videoTitle = metadata["Title"]!!
//            metadata["Duration"] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let { durationMs ->
//                (durationMs.toLong() / 1000).toString() + " seconds"
//            } ?: "Unknown"
//            videoDuration = metadata["Duration"]!!.toInt()
//            isVideoDataLoading = false
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            metadata["Error"] = "Failed to retrieve metadata: ${e.message}"
//            error = metadata["Error"]!!
//            isVideoDataLoading = false
//        } finally {
//            // Release the retriever
//            metadataRetriever.release()
//            Log.d("videodata", "$videoTitle $videoDuration")
//            isVideoDataLoading = false
//        }
//
//        Log.d("metadata", metadata.toString())
//    }


}