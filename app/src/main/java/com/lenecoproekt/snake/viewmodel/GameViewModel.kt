package com.lenecoproekt.snake.viewmodel

import androidx.lifecycle.ViewModel
import com.lenecoproekt.snake.logic.Result
import com.lenecoproekt.snake.logic.SnakeGame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

class GameViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }
    private val snakeGame = SnakeGame()
    private val gameFieldChannel by lazy { runBlocking { snakeGame.sendData() } }

    init{
        launch{
            snakeGame.startGame()
            gameFieldChannel.consumeEach { result ->
                when (result) {
                    is Result.Success<*> -> {
                        setData(result.data as Array<Array<String?>>)
                    }
                    is Result.Error -> setError(result.error)
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
    override fun onCleared() {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }
}