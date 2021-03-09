package com.lenecoproekt.snake.logic

import com.lenecoproekt.snake.*
import com.lenecoproekt.snake.logic.gameObjects.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class SnakeGame : CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }

    private var snake = Snake(WIDTH / 2, HEIGHT / 2)

    var gameOver = false
        private set
    var win = false
        private set
    var score = 0
        private set

    private var turnDelay = 300L
    private var goal = 28

    private lateinit var apple: Apple
    private lateinit var mushroom: Mushroom
    private lateinit var timeFreezer: TimeFreezer
    private lateinit var bomb: Bomb

    val gameField: Array<Array<String?>> = Array(HEIGHT) { arrayOfNulls(WIDTH) }

    private fun clear() {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                    gameField[i][j] = ""
            }
        }
    }

    public fun startGame(): Array<Array<String?>> {
        initialize()
        return gameField
    }

    private fun initialize() {
        clear()
        loadSnake()
        createNewApple()
        createNewMushroom()
        createNewTimeFreezer()
        createNewBomb()
    }

    private fun loadSnake() {
        gameField[snake.snakeParts[0].x][snake.snakeParts[0].y] = SNAKE_HEAD
        for (i in 1 until snake.snakeParts.size){
            gameField[snake.snakeParts[i].x][snake.snakeParts[i].y] = SNAKE_BODY
        }
    }

    private fun createNewBomb() {
        do {
            bomb = Bomb(Random(WIDTH).nextInt(), Random(HEIGHT).nextInt());
        } while ((snake.checkCollision(bomb)) || ((mushroom.x == bomb.x) && (mushroom.y == bomb.y))
            || ((apple.x == bomb.x) && (apple.y == bomb.y)) || ((timeFreezer.x == bomb.x) && (timeFreezer.y == bomb.y))
        )
        gameField[bomb.x][bomb.y] = BOMB
    }

    private fun createNewTimeFreezer() {
        if (!timeFreezer.isAlive) {
            do {
                timeFreezer = TimeFreezer(Random(WIDTH).nextInt(), Random(HEIGHT).nextInt());
            } while ((snake.checkCollision(timeFreezer)) || ((mushroom.x == timeFreezer.x) && (mushroom.y == timeFreezer.y))
                || ((apple.x == timeFreezer.x) && (apple.y == timeFreezer.y))
            )
        }
        gameField[timeFreezer.x][timeFreezer.y] = TIME_FREEZER
    }

    private fun createNewMushroom() {
        if (!mushroom.isAlive) {
            do {
                mushroom = Mushroom(Random(WIDTH).nextInt(), Random(HEIGHT).nextInt())
            } while (snake.checkCollision(mushroom) || ((mushroom.x == apple.x) && (mushroom.y == apple.y))
            )
        }
        gameField[mushroom.x][mushroom.y] = MUSHROOM
    }

    private fun createNewApple() {
        if (!apple.isAlive) {
            do {
                apple = Apple(Random(WIDTH).nextInt(), Random(HEIGHT).nextInt())
            } while (snake.checkCollision(apple)
            )
        }
        gameField[apple.x][apple.y] = APPLE
    }

    private suspend fun nextTurn(tDelay: Long) :  Array<Array<String?>>{
        delay(tDelay)
        snake.move(apple, mushroom, timeFreezer, bomb)
        if (!apple.isAlive) {
            initialize()
            score += 5
            turnDelay -= 10
        }
        if (!mushroom.isAlive){
            initialize()
            score -= 5
        }
        if (!timeFreezer.isAlive){
            initialize()
            turnDelay += 10
        }
        if (!bomb.isAlive) gameOver = true
        if (!snake.isAlive) gameOver = true
        if (snake.getLength() > goal) win = true
        return gameField
    }

    fun setSnakeDirection(direction: Direction) {
        when(direction){
            Direction.UP -> snake.setDirection(Direction.UP)
            Direction.DOWN -> snake.setDirection(Direction.DOWN)
            Direction.LEFT -> snake.setDirection(Direction.LEFT)
            Direction.RIGHT -> snake.setDirection(Direction.RIGHT)
        }
    }
}