package com.lenecoproekt.snake.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RatingBar
import android.widget.SeekBar
import com.lenecoproekt.snake.AppPreferences
import com.lenecoproekt.snake.R
import com.lenecoproekt.snake.databinding.ActivityMainBinding
import com.lenecoproekt.snake.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private val ui: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ui.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                AppPreferences.wall = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        ui.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            AppPreferences.gameDiff = rating.toInt()
        }
    }
}