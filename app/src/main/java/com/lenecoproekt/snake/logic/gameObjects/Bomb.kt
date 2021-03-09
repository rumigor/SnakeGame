package com.lenecoproekt.snake.logic.gameObjects

data class Bomb(override var x : Int, override var y : Int) : GameObject(x,y) {
    var isAlive = true
}