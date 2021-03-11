package com.lenecoproekt.snake.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lenecoproekt.snake.logic.Direction
import com.lenecoproekt.snake.logic.SnakeGame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class GameViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }
    private val snakeGame = SnakeGame()

    init {
        launch { snakeGame.startGame() }
        launch {
            snakeGame.stateData.collect {
                try {
                    setData(it)
                } catch (e: Exception) {
                    setError(e)
                }
            }

        }


    }

    private val viewStateChannel = BroadcastChannel<Array<Array<String?>>>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    fun getViewState(): ReceiveChannel<Array<Array<String?>>> = viewStateChannel.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    private fun setError(e: Throwable) {
        launch {
            errorChannel.send(e)
        }
    }

    private fun setData(data: Array<Array<String?>>) {
        launch {
            viewStateChannel.send(data)
        }
    }

    fun setSnakeDirection(i: Int, j: Int) {
        if (getDirection() == Direction.UP || getDirection() == Direction.DOWN) {
            if (snakeGame.snake.y < j) snakeGame.setSnakeDirection(Direction.RIGHT)
            else snakeGame.setSnakeDirection(Direction.LEFT)
        } else {
            if (snakeGame.snake.x < i) snakeGame.setSnakeDirection(Direction.DOWN)
            else snakeGame.setSnakeDirection(Direction.UP)
        }
    }

    private fun getDirection() = snakeGame.getDirection()

    override fun onCleared() {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }
}