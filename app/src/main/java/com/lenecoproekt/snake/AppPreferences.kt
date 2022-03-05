package com.lenecoproekt.snake

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("SnakeGameApp.sharedprefs", MODE_PRIVATE)
    }

    var wall: Int?
        get() = Key.WALL.getInt()
        set(value) = Key.WALL.setInt(value)

    var gameDiff: Int?
        get() = Key.DIFFICULTY.getInt()
        set(value) = Key.DIFFICULTY.setInt(value)




    private enum class Key {
        WALL, DIFFICULTY;

        fun getInt(): Int? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getInt(name, 0) else null

        fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()

        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}