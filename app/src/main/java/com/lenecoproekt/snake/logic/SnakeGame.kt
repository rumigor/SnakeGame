package com.lenecoproekt.snake.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lenecoproekt.snake.*
import com.lenecoproekt.snake.logic.gameObjects.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class SnakeGame : CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }

    var snake = Snake(WIDTH / 2, HEIGHT / 2)
        private set

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

    val gameField: Array<Array<String?>> = Array(HEIGHT) { arrayOf("", "", "", "", "", "", "", "",
        "","","","","","","") }

    private var _stateData = MutableStateFlow(gameField)

    val stateData: StateFlow<Array<Array<String?>>> = _stateData


    init{
        apple.isAlive = false
        mushroom.isAlive = false
        timeFreezer.isAlive = false
        bomb.isAlive = false
    }

    private fun loadField() {
        for (i in 0 until HEIGHT) {
            for (j in 0 until WIDTH) {
                gameField[i][j] = ""
            }
        }
    }

    public suspend fun startGame() {
        initialize()
        while (!gameOver && !win) {
            delay(turnDelay)
            nextTurn()
        }
    }


    private fun initialize() {
        loadField()
        loadField()
        loadSnake()
        createNewApple()
        createNewMushroom()
        createNewTimeFreezer()
        createNewBomb()
        _stateData.value = gameField
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

    private fun nextTurn() {
        snake.move(apple, mushroom, timeFreezer, bomb)
        if (!apple.isAlive) {
            initialize()
            score += 5
            turnDelay -= 10
            _stateData.value = gameField
            return
        }
        if (!mushroom.isAlive) {
            initialize()
            score -= 5
            _stateData.value = gameField
            return
        }
        if (!timeFreezer.isAlive) {
            initialize()
            turnDelay += 10
            _stateData.value = gameField
            return
        }
        if (!bomb.isAlive) gameOver = true
        if (!snake.isAlive) gameOver = true
        if (snake.getLength() > goal) win = true
        initialize()
        _stateData.value = gameField
    }

    fun setSnakeDirection(direction: Direction) {
        when (direction) {
            Direction.UP -> snake.changeDirection(Direction.UP)
            Direction.DOWN -> snake.changeDirection(Direction.DOWN)
            Direction.LEFT -> snake.changeDirection(Direction.LEFT)
            Direction.RIGHT -> snake.changeDirection(Direction.RIGHT)
        }
    }

    fun getDirection() = snake.direction
}