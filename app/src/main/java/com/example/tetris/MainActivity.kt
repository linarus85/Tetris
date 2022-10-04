package com.example.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tetris.databinding.ActivityMainBinding
import com.example.tetris.storage.AppPreferrences
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
  var tvHighScore: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        tvHighScore = findViewById<Button>(R.id.tv_high_score)
        btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        btnExit.setOnClickListener(this::onBtnExitClick)

    }

    private fun onBtnExitClick(view: View) {
        System.exit(0)
    }

    private fun onBtnNewGameClick(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}