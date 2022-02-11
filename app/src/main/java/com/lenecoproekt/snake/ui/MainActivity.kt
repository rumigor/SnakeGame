package com.lenecoproekt.snake.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.lenecoproekt.snake.R
import com.lenecoproekt.snake.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val start = findViewById<Button>(R.id.startButton)
        start.setOnClickListener {
            startActivity(GameActivity.getStartIntent(this))
        }
    }
}