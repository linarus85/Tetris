package com.example.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.models.AppModel
import com.example.tetris.storage.AppPreferrences
import com.example.tetris.views.TetrisView

class GameActivity : AppCompatActivity() {
    var tvHighScore: TextView? = null
    var tvCurrentScore: TextView? = null
    var appPreferences: AppPreferrences? = null
    private lateinit var tetrisView: TetrisView
    private val appModel: AppModel = AppModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        appPreferences = AppPreferrences(this)
        appModel.setPreferences(appPreferences)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tetrisView = findViewById<TetrisView>(R.id.view_tetris)
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisView)
        updateHigeScore()
        updateCurrentScore()
    }

    private fun onTetrisView(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOwer() || appModel.isGameAwaitStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motion.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveToutchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motion.LEFT)
                1 -> moveTetromino(AppModel.Motion.ROTATE)
                2 -> moveTetromino(AppModel.Motion.DOWN)
                3 -> moveTetromino(AppModel.Motion.RIGHT)
            }
        }
        return true
    }

    private fun moveTetromino(motion: AppModel.Motion) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun resolveToutchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int
        direction = if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
        return direction
    }


    private fun updateHigeScore() {
    tvHighScore?.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore() {
      tvCurrentScore?.text = "0"
    }
}




