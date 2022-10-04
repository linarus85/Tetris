package com.example.tetris.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferrences(context: Context) {
    var data: SharedPreferences =
        context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveHighScore(score: Int) {
        data.edit().putInt("HIGH_SCORE", score).apply()
    }

    fun getHighScore(): Int {
        return data.getInt("HIGH_SCORE", 0)
    }

    fun clearHighScore() {
        data.edit().putInt("HIGH_SCORE", 0).apply()
    }
}