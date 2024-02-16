package br.com.raulreis.videotube

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.SurfaceView
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.videotube.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var videoAdapter: VideoAdapter

    private lateinit var youtubePlayer: YoutubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when(resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    binding.imgBackLogo.imageTintList = ColorStateList.valueOf(Color.WHITE)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    window.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                    window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
                }
            }
        }

        val videos = mutableListOf<Video>()
        videoAdapter = VideoAdapter(videos) {video: Video ->
            showOverlayView(video)
        }

        with(binding) {
            rvMain.layoutManager = LinearLayoutManager(this@MainActivity)
            rvMain.adapter = videoAdapter
            findViewById<View>(R.id.viewLayer).alpha = 0f
        }

        CoroutineScope(Dispatchers.IO).launch {
            val res = async { getVideos() }
            val listVideos = res.await()
            withContext(Dispatchers.Main) {
                listVideos?.let {
                    videos.clear()
                    videos.addAll(listVideos.data)
                    videoAdapter.notifyDataSetChanged()
                    binding.containerMotion.removeView(binding.FrameProgress)
                    //binding.FrameProgress.visibility = View.GONE
                }
            }
        }

        findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    youtubePlayer.seek(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        preparePlayer()

    }

    override fun onDestroy() {
        super.onDestroy()
        youtubePlayer.release()
    }

    override fun onPause() {
        super.onPause()
        youtubePlayer.pause()
    }

    private fun preparePlayer() {
        youtubePlayer = YoutubePlayer(this@MainActivity)
        youtubePlayer.youtubePlayerListener = object : YoutubePlayer.YoutubePlayerListener {
            override fun onPrepared(duration: Long) {
                findViewById<TextView>(R.id.txvDurationTime).text = duration.formatTime()
            }

            override fun onTrackTime(currentPosition: Long, percent: Long) {
                findViewById<SeekBar>(R.id.seekbar).progress = percent.toInt()
                findViewById<TextView>(R.id.txvCurrentTime).text = currentPosition.formatTime()
            }

        }
        findViewById<SurfaceView>(R.id.surfaceVideoPlayer).holder.addCallback(youtubePlayer)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun showOverlayView(video: Video) {
        val viewLayer = findViewById<View>(R.id.viewLayer)
        viewLayer.animate().apply {
                duration = 400
                alpha(0.5f)
        }

        binding.containerMotion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
            ) {
                if (progress > 0.5f)
                    viewLayer.alpha = 1.0f - progress
                else
                    viewLayer.alpha = 0.5f

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
            ) {

            }
        })

        findViewById<ImageView>(R.id.imgVideoPlayer).visibility = View.GONE
        youtubePlayer.setUrl(video.videoUrl)

        val detailAdapter = VideoDetailAdapter(videos())

        with(findViewById<RecyclerView>(R.id.rvSimilar)) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = detailAdapter
            detailAdapter.notifyDataSetChanged()
        }

        findViewById<TextView>(R.id.txvContentChannel).text = video.publisher.name
        findViewById<TextView>(R.id.txvContentTitle).text = video.title
        Picasso.get().load(video.publisher.pictureProfileUrl).into(findViewById<ImageView>(R.id.imgChannel))


    }

    private fun getVideos(): ListVideo? {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .get()
            .url("https://tiagoaguiar.co/api/youtube-videos")
            .build()

        return try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                GsonBuilder().create()
                    .fromJson(response.body()?.string(), ListVideo::class.java)
            }
            else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}