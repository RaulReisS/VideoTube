package br.com.raulreis.videotube

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.SurfaceHolder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class YoutubePlayer (private val context: Context) : SurfaceHolder.Callback {

    private var mediaPlayer : SimpleExoPlayer? = null

    var youtubePlayerListener : YoutubePlayerListener? = null

    private lateinit var runnable: Runnable

    private val handler = Handler()

    override fun surfaceCreated(holder: SurfaceHolder) {
        if ( mediaPlayer == null) {
            mediaPlayer = SimpleExoPlayer.Builder(context).build()

            mediaPlayer?.setVideoSurfaceHolder(holder)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer?.release()
    }

    fun setUrl(url: String) {

        val testeUrl = "https://firebasestorage.googleapis.com/v0/b/instaapp-kotlin.appspot.com/o/video%2FIMG_1839.MP4?alt=media&token=434ba18f-dfc0-401f-a99b-f34ba1dde87c"

        mediaPlayer?.let {
            val dataSourceFactory = DefaultDataSourceFactory (
                context,
                Util.getUserAgent(context, "VideoTube")
                    )

            val videoSource : MediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(testeUrl)))

            it.prepare(videoSource)
            it.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        youtubePlayerListener?.onPrepared(it.duration)
                        trackTime()
                    }
                }
            })
            play()
        }
    }

    private fun trackTime() {
        mediaPlayer?.let {
            youtubePlayerListener?.onTrackTime(it.currentPosition,
                it.currentPosition * 100 / it.duration)


            if (it.isPlaying) {
                runnable = Runnable {
                    trackTime()
                }
                handler.postDelayed(runnable, 1000)
            }
        }
    }

    fun play() {
        mediaPlayer?.playWhenReady = true
    }

    fun pause() {
        mediaPlayer?.playWhenReady = false
    }

    fun release() {
        mediaPlayer?.release()
    }

    fun seek(progress: Long) {
        if (progress > 0) {
            mediaPlayer?.let {
                val seek = progress * it.duration / 100
                it.seekTo(seek)
            }
        }
    }

    interface YoutubePlayerListener {
        fun onPrepared(duration: Long)
        fun onTrackTime(currentPosition: Long, percent: Long)
    }

}