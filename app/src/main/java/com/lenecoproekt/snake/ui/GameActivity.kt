package com.lenecoproekt.snake.ui

import android.content.Context
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lenecoproekt.snake.*
import com.lenecoproekt.snake.databinding.ActivityGameBinding
import com.lenecoproekt.snake.logic.Direction
import com.lenecoproekt.snake.viewmodel.GameViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

class GameActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }
    private lateinit var dataJob: Job
    private lateinit var errorJob: Job

    private lateinit var cells: Array<Array<TextView?>>

    private val viewModel: GameViewModel by lazy { ViewModelProvider(this).get(GameViewModel::class.java) }

    private val ui: ActivityGameBinding by lazy { ActivityGameBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        cells = Array(HEIGHT) { arrayOfNulls(WIDTH) }
        ui.score.text = POINTS
        ui.snakeLength.text = SNAKE_LENGTH
        ui.timer.text = TIMER
        initializeGameField()
        ui.chronometer.start()
        ui.restart.setOnClickListener{
            recreate()
        }
    }

    private fun renderData(gameField: Array<Array<String?>>) {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                when (gameField[i][j]){
                    SNAKE_HEAD -> {
                        cells[i][j]?.setBackgroundResource(R.drawable.ic_baseline_android_24)
                        cells[i][j]?.text = ""
                    }
                    SNAKE_BODY -> cells[i][j]?.setBackgroundResource(R.drawable.ic_snake_body_1_24)
                    "" -> {
                        cells[i][j]?.setBackgroundResource(R.drawable.cell)
                        cells[i][j]?.text=""
                    }
                    "X" -> cells[i][j]?.setBackgroundResource(R.drawable.ic_baseline_cancel_24)
                    else -> {
                        cells[i][j]?.setBackgroundColor(resources.getColor(R.color.white, theme))
                        cells[i][j]?.text = gameField[i][j]
                    }
                }
            }
        }
        if (viewModel.isGameOver() || viewModel.isWin()) {
            ui.chronometer.stop()
            if (viewModel.isGameOver()) ui.gameProgress.text = "GAME OVER!"
            if (viewModel.isWin()) ui.gameProgress.text = "YOU WON!"
        }
        ui.length.text = viewModel.getSnakeLength().toString()
        ui.scoreN.text = viewModel.getScore().toString()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeGameField() {
        for (i in 0 until HEIGHT) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
            for (j in 0 until WIDTH) {
                val cell = TextView(this)
                cell.setBackgroundResource(R.drawable.ic_baseline_grass_24)
                cell.text = ""
                val display = this.display
                val size = Point()
                display?.getRealSize(size)
                val scrWidth = size.x
                val scrHeight = size.y
                val dm = resources.displayMetrics
//                cell.textSize = (scrHeight.toFloat() / scrWidth.toFloat()) * (dm.densityDpi) / ((WIDTH + HEIGHT).toFloat())
                cell.textSize = 22f
                println(cell.textSize)
                cell.textAlignment = View.TEXT_ALIGNMENT_CENTER
                cells[i][j] = cell
                tableRow.addView(cell, j)
                cells[i][j]?.setOnClickListener {
                    viewModel.setSnakeDirection(i, j)
                }
            }
            ui.gameField.addView(tableRow, i)
            ui.gameField.isShrinkAllColumns = true
            ui.gameField.isStretchAllColumns = true
        }

    }

    override fun onStart() {
        super.onStart()
        dataJob = launch {
            viewModel.getViewState().consumeEach {
                renderData(it)
            }
        }

        errorJob = launch {
            viewModel.getErrorChannel().consumeEach {
                renderError(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dataJob.cancel()
        errorJob.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.ok_bth_title) { dismiss() }
            show()
        }
    }

    private fun renderError(error: Throwable) {
        error.message?.let { showError(it) }

    }
}