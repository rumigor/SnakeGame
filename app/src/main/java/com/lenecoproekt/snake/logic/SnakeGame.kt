package com.lenecoproekt.snake.logic

import com.lenecoproekt.snake.*
import com.lenecoproekt.snake.logic.gameObjects.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
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

    private var apple = Apple(0, 0)
    private var mushroom = Mushroom(0, 0)
    private var timeFreezer = TimeFreezer(0, 0)
    private var bomb = Bomb(0, 0)

    val gameField: Array<Array<String?>> = Array(HEIGHT) { arrayOfNulls(WIDTH) }

    private fun clear() {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                gameField[i][j] = ""
            }
        }
    }

    public suspend fun startGame() {
        initialize()
        while (!gameOver && !win) {
            nextTurn(turnDelay)
            sendData()
        }
    }

    suspend fun sendData(): ReceiveChannel<Result> =
        Channel<Result>(Channel.CONFLATED).apply {
            try {
                offer(Result.Success(gameField))
            } catch (e: Throwable) {
                offer(Result.Error(e))
            }
        }

    private suspend fun initialize() {
        clear()
        loadSnake()
        apple.isAlive = false
        mushroom.isAlive = false
        timeFreezer.isAlive = false
        bomb.isAlive = false
        createNewApple()
        createNewMushroom()
        createNewTimeFreezer()
        createNewBomb()
        sendData()
    }

    private fun loadSnake() {
        gameField[snake.snakeParts[0].x][snake.snakeParts[0].y] = SNAKE_HEAD
        for (i in 1 until snake.snakeParts.size) {
            gameField[snake.snakeParts[i].x][snake.snakeParts[i].y] = SNAKE_BODY
        }
    }

    private fun createNewBomb() {
        do {
            bomb = Bomb(Random.nextInt(WIDTH), Random.nextInt(HEIGHT));
        } while ((snake.checkCollision(bomb)) || ((mushroom.x == bomb.x) && (mushroom.y == bomb.y))
            || ((apple.x == bomb.x) && (apple.y == bomb.y)) || ((timeFreezer.x == bomb.x) && (timeFreezer.y == bomb.y))
        )
        gameField[bomb.x][bomb.y] = BOMB
    }

    private fun createNewTimeFreezer() {
        if (!timeFreezer.isAlive) {
            do {
                timeFreezer = TimeFreezer(Random.nextInt(WIDTH), Random.nextInt(HEIGHT))
            } while ((snake.checkCollision(timeFreezer)) || ((mushroom.x == timeFreezer.x) && (mushroom.y == timeFreezer.y))
                || ((apple.x == timeFreezer.x) && (apple.y == timeFreezer.y))
            )
        }
        gameField[timeFreezer.x][timeFreezer.y] = TIME_FREEZER
    }

    private fun createNewMushroom() {
        if (!mushroom.isAlive) {
            do {
                mushroom = Mushroom(Random.nextInt(WIDTH), Random.nextInt(HEIGHT))
            } while (snake.checkCollision(mushroom) || ((mushroom.x == apple.x) && (mushroom.y == apple.y))
            )
        }
        gameField[mushroom.x][mushroom.y] = MUSHROOM
    }

    private fun createNewApple() {
        if (!apple.isAlive) {
            do {
                apple = Apple(Random.nextInt(WIDTH), Random.nextInt(HEIGHT))
            } while (snake.checkCollision(apple)
            )
        }
        gameField[apple.x][apple.y] = APPLE
    }

    private suspend fun nextTurn(tDelay: Long): Array<Array<String?>> {
        delay(tDelay)
        snake.move(apple, mushroom, timeFreezer, bomb)
        if (!apple.isAlive) {
            initialize()
            score += 5
            turnDelay -= 10
        }
        if (!mushroom.isAlive) {
            initialize()
            score -= 5
        }
        if (!timeFreezer.isAlive) {
            initialize()
            turnDelay += 10
        }
        if (!bomb.isAlive) gameOver = true
        if (!snake.isAlive) gameOver = true
        if (snake.getLength() > goal) win = true
        return gameField
    }

    fun setSnakeDirection(direction: Direction) {
        when (direction) {
            Direction.UP -> snake.setDirection(Direction.UP)
            Direction.DOWN -> snake.setDirection(Direction.DOWN)
            Direction.LEFT -> snake.setDirection(Direction.LEFT)
            Direction.RIGHT -> snake.setDirection(Direction.RIGHT)
        }
    }
}