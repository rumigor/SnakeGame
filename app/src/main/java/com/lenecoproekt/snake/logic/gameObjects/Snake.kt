package com.lenecoproekt.snake.logic.gameObjects

import com.lenecoproekt.snake.HEIGHT
import com.lenecoproekt.snake.WIDTH
import com.lenecoproekt.snake.logic.Direction

data class Snake(var x: Int, var y: Int) {
    var isAlive = true
    private var direction = Direction.LEFT
    var snakeParts =
        mutableListOf(GameObject(x, y), GameObject(x, y), GameObject(x, y))
        private set

    public fun setDirection(direction: Direction) {
        when (this.direction) {
            Direction.RIGHT -> if (direction != Direction.LEFT || (snakeParts[0].x != snakeParts[1].x))
                this.direction = direction
            Direction.LEFT -> if (direction != Direction.RIGHT || (snakeParts[0].x != snakeParts[1].x))
                this.direction = direction
            Direction.UP -> if (direction != Direction.DOWN || (snakeParts[0].y != snakeParts[1].y))
                this.direction = direction
            Direction.DOWN -> if (direction != Direction.UP || (snakeParts[0].y != snakeParts[1].y))
                this.direction = direction
        }
    }

    public fun move(apple: Apple, mushroom: Mushroom, timeFreezer: TimeFreezer, bomb: Bomb) {
        var snakeHead = createNewHead()
        if ((snakeHead.x < 0) || (snakeHead.x >= WIDTH) || (snakeHead.y < 0) || (snakeHead.y >= HEIGHT)) isAlive =
            false
        else {
            if (checkCollision(snakeHead)) isAlive = false
            else {
                snakeParts.add(0, snakeHead)
                if ((apple.x == snakeHead.x) && (apple.y == snakeHead.y)) {
                    apple.isAlive = false
                } else if ((mushroom.x == snakeHead.x) && (mushroom.y == snakeHead.y)) {
                    mushroom.isAlive = false
                    removeTail()
                    if (snakeParts.size > 2) removeTail()
                } else if ((timeFreezer.x == snakeHead.x) && (timeFreezer.y == snakeHead.y)) {
                    timeFreezer.isAlive = false
                    removeTail()
                } else if ((bomb.x == snakeHead.x) && (bomb.y == snakeHead.y)) {
                    bomb.isAlive = false
                } else removeTail()
            }
        }
    }

    private fun removeTail() {
        snakeParts.removeAt(snakeParts.size - 1)
    }

    private fun createNewHead(): GameObject = when (direction) {
        Direction.LEFT -> GameObject(snakeParts[0].x - 1, snakeParts[0].y)
        Direction.RIGHT -> GameObject(snakeParts[0].x + 1, snakeParts[0].y)
        Direction.UP -> GameObject(snakeParts[0].x, snakeParts[0].y - 1)
        Direction.DOWN -> GameObject(snakeParts[0].x, snakeParts[0].y + 1)
    }

    public fun checkCollision(gameObject: GameObject): Boolean {
        var collided = false
        for (i in snakeParts.indices) {
            collided = ((gameObject.x == snakeParts[i].x) && (gameObject.y == snakeParts[i].y))
        }
        return collided
    }

    public fun getLength() = snakeParts.size
}