@file:Suppress("DEPRECATION")

package com.example.media_player

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.media_player.model.Music
import java.text.SimpleDateFormat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var btnPlay: Button
    private lateinit var url: String
    private lateinit var btnNext: Button
    private lateinit var btnPre: Button
    private lateinit var seekbar: SeekBar
    private lateinit var tvTotalTime: TextView
    private lateinit var shuffle: ImageView
    private lateinit var loop: ImageView
    private lateinit var tvCurentTime: TextView
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var isPlaying = false
    private var position = 0
    private var repeat = false
    private var checkRandom = false
    private var next = false
    val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        val music1 = Music("https://nguyenhuuloc99.000webhostapp.com/music/anhkhongthathu.mp3")
        val music2 = Music("https://nguyenhuuloc99.000webhostapp.com/music/banduyen.mp3")
        val music3 = Music("https://nguyenhuuloc99.000webhostapp.com/music/dungnoi.mp3")
        val list = mutableListOf<Music>()
        list.add(music1)
        list.add(music2)
        list.add(music3)
        url = music1.url
        btnPlay.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
            } else {
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                PlayMp3().execute(list[position].url)
                updateSeeBar()
            }
        }
        btnNext.setOnClickListener {
            if (list.size > 0) {
                if (mediaPlayer!!.isPlaying || mediaPlayer != null) {
                    mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    mediaPlayer = null
                }
                if (position < list.size) {
                    position++
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                    if (repeat) {
                        if (position == 0) {
                            position = list.size
                        }
                        position -= 1
                    }
                    if (checkRandom) {
                        val index = Random.nextInt(list.size)
                        if (index == position) {
                            position = index - 1
                        }
                        position = index
                    }
                    if (position > list.size - 1) {
                        position = 0
                    }
                    PlayMp3().execute(list[position].url)
                }
            }
        }
        btnPre.setOnClickListener {
            if (list.size > 0) {
                if (mediaPlayer!!.isPlaying || mediaPlayer != null) {
                    mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    mediaPlayer = null
                }
                if (position < list.size) {
                    position--
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                    if (repeat) {
                        if (position == 0) {
                            position = list.size
                        }
                        position += 1
                    }
                    if (checkRandom) {
                        val index = Random.nextInt(list.size)
                        if (index == position) {
                            position = index - 1
                        }
                        position = index
                    }
                    if (position == 0) {
                        position = list.size - 1
                    }
                    if (position > list.size - 1) {
                        position = 0
                    }
                    PlayMp3().execute(list[position].url)
                }
            }
        }
        //shuffle
        shuffle.setOnClickListener {
            if (checkRandom) {
                checkRandom = false
                shuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24)
            } else {
                checkRandom = true
                shuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_2)
            }
        }
        loop.setOnClickListener {
            if (repeat) {
                repeat = false
                loop.setBackgroundResource(R.drawable.ic_baseline_loop_24)
            } else {
                repeat = true
                loop.setBackgroundResource(R.drawable.ic_baseline_loop_2)
            }
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mediaPlayer?.seekTo(seekbar.progress)
            }

        })
    }

    private fun init() {
        btnPlay = findViewById(R.id.btn_play)
        btnNext = findViewById(R.id.btn_next)
        btnPre = findViewById(R.id.btn_pre)
        seekbar = findViewById(R.id.seekbar)
        tvTotalTime = findViewById(R.id.tv_total_time)
        tvCurentTime = findViewById(R.id.tv_curent_time)
        shuffle = findViewById(R.id.shuffle)
        loop = findViewById(R.id.loop)
    }

    private val update = Runnable {
        updateSeeBar()
        val currentDuration = mediaPlayer!!.currentPosition
        tvCurentTime.text = miliSecondToTime(currentDuration.toLong())

    }

    fun miliSecondToTime(miliSeconds: Long): String {
        val seconds: Long = miliSeconds / 1000 % 60
        val minutes: Long = miliSeconds / (1000 * 60) % 60

        val b = StringBuilder()
        b.append(if (minutes == 0L) "00" else if (minutes < 10) "0$minutes" else minutes.toString())
        b.append(":")
        b.append(if (seconds == 0L) "00" else if (seconds < 10) "0$seconds" else seconds.toString())
        return b.toString()
    }

    private fun updateSeeBar() {
        if (mediaPlayer!!.isPlaying) {
            seekbar.progress =
                (((mediaPlayer!!.currentPosition / mediaPlayer!!.duration)).toFloat() * 100).toInt()
        }
        handler.postDelayed(update, 1000)
    }

    @SuppressLint("StaticFieldLeak")
    inner class PlayMp3 : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java", ReplaceWith("strings.get(0)"))
        override fun doInBackground(vararg strings: String?): String? {
            return strings[0]
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setOnCompletionListener {
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
            }
            mediaPlayer?.apply {
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA).build()
                )
                try {
                    setDataSource(result)
                    prepare()
                    start()
                } catch (e: java.lang.Exception) {
                    Log.e(">>>", e.toString())
                }
            }
            TimeSong()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun TimeSong() {
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        tvTotalTime.text = simpleDateFormat.format(mediaPlayer?.duration ?: "0:0")
        seekbar.max = mediaPlayer?.duration ?: 0
    }
}