package com.example.mediapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var seekBar: SeekBar
    private lateinit var backBtn: Button
    private val handler = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        playBtn = findViewById(R.id.btnPlay)
        pauseBtn = findViewById(R.id.btnPause)
        seekBar = findViewById(R.id.seekBar)
        backBtn = findViewById<Button>(R.id.btnBack)

        mediaPlayer = MediaPlayer.create(this, R.raw.meditation_music)
        seekBar.max = mediaPlayer.duration

        playBtn.setOnClickListener {
            mediaPlayer.start()
        }

        pauseBtn.setOnClickListener {
            mediaPlayer.pause()
        }


        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }



        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.cancel()
        mediaPlayer.release()
    }
}
