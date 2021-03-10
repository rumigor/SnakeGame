package com.lenecoproekt.snake.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lenecoproekt.snake.*
import com.lenecoproekt.snake.databinding.ActivityGameBinding
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

    private val ui: ActivityGameBinding by lazy{ActivityGameBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        cells = Array(HEIGHT) { arrayOfNulls(WIDTH) }
        ui.score.text = POINTS
        ui.snakeLength.text = SNAKE_LENGTH
        initializeGameField()
    }

    private fun renderData(gameField: Array<Array<String?>>) {
        for (i in 0 until HEIGHT){
            for(j in 0 until WIDTH){
                cells[i][j]?.text = gameField[i][j]
            }
        }
    }

    private fun initializeGameField() {
        for (i in 0 until HEIGHT){
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
            for (j in 0 until WIDTH){
                val cell = TextView(this)
                cell.setBackgroundColor(resources.getColor(R.color.design_default_color_primary, theme))
                cell.text = "2"
                cell.textSize = 24 / ((WIDTH + HEIGHT).toFloat() / 20)
                cell.textAlignment = View.TEXT_ALIGNMENT_CENTER
                cells[i][j] = cell
                tableRow.addView(cell, j)
                cell.setOnClickListener{
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