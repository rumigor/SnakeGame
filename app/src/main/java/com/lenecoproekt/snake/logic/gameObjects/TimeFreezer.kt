package com.lenecoproekt.snake.logic.gameObjects

data class TimeFreezer(override var x: Int, override var y: Int) : GameObject(x,y) {
    var isAlive = true
}