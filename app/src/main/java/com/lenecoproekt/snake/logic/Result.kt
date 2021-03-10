package com.lenecoproekt.snake.logic

sealed class Result{
    data class Success<out T>(val data: T) : Result()
    data class Error(val error: Throwable) : Result()
}