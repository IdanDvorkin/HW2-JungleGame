package com.example.junglegame.Activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.junglegame.R

class MainMenuActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.menu_music
        )
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        val sensorButton: Button = findViewById(R.id.button_sensor_mode)
        sensorButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("controlMode", "sensor")
            mediaPlayer.pause()
            startActivity(intent)
        }
        val buttonModeButton: Button = findViewById(R.id.button_button_mode)
        buttonModeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("controlMode", "button")
            mediaPlayer.pause()
            startActivity(intent)
        }
        val fastModeButton: Button = findViewById(R.id.button_fast_mode)
        fastModeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("controlMode", "fast")
            mediaPlayer.pause()
            startActivity(intent)
        }
        val highScoresButton: Button = findViewById(R.id.high_scores_button)
        highScoresButton.setOnClickListener {
            val intent = Intent(this, HighScoreActivity::class.java)
            mediaPlayer.pause()
            startActivity(intent)
        }
        val exitButton: Button = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            mediaPlayer.pause()
            finishAffinity()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
