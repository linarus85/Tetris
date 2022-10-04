package com.example.tetris.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.example.tetris.GameActivity
import com.example.tetris.R
import com.example.tetris.models.AppModel
import com.example.tetris.models.Block
import com.example.tetris.enums.FieldConstans


class TetrisView : View {


    private val paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var activity: GameActivity? = null

    //    private val viewHandler: ViewHandler(this)
    private var cellSize: Dimensionion = Dimensionion(0, 0)
    private var frameOfSet: Dimensionion = Dimensionion(0, 0)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
            super(context, attrs, defStyle)

    companion object {
        private val DELAY = 500
        private val BLOCK_OFFSET = 2
        private val FRAME_OFFSET = 10
    }

    private class ViewHandler(private val owner: TetrisView) : Handler() {
        override fun handleMessage(message: Message) {
            if (message.what == 0) {
                if (owner.model != null) {
                    if (owner.model!!.isGameOwer()) {
                        owner.model?.endGame()
                        Toast.makeText(
                            owner.activity, R.string.game_over,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (owner.model!!.isGameActive()) {
                        owner.setGameCommandWithDelay(AppModel.Motion.DOWN)
                    }
                }
            }
        }

        fun sleep(delay: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }

    }

    private data class Dimensionion(val width: Int, val height: Int)

    fun setGameCommandWithDelay(move: AppModel.Motion) {
        val now = System.currentTimeMillis()
        if (now - lastMove > DELAY) {
            model?.generateField(move.name)
            invalidate()
            lastMove = now
        }
        updateScore()
        val viewHandler = ViewHandler(this)
        viewHandler.sleep(DELAY.toLong())
    }

    private fun updateScore() {
        activity?.tvCurrentScore?.text = "${model?.score}"
        activity?.tvHighScore?.text =
            "${activity?.appPreferences?.getHighScore()}"
    }

    fun setGameCommand(move: AppModel.Motion) {
        if (null != model && (model?.currentState == AppModel.Statuses.ACTIVE.name)) {
            if (AppModel.Motion.DOWN == move) {
                model?.generateField(move.name)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }
    }

    fun setModel(model: AppModel) {
        this.model = model
    }

    fun setActivity(gameActivity: GameActivity) {
        this.activity = gameActivity
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)
        if (model != null) {
            for (i in 0 until FieldConstans.ROW_COUNT.value) {
                for (j in 0 until FieldConstans.COLUMN_COUNT.value) {
                    drawCell(canvas, i, j)
                }
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val cellStatus = model?.getCellStatus(row, col)
        if (AppModel.CellConstants.EMPTY.value != cellStatus) {
            val color = if (AppModel.CellConstants.EPEMERAL.value == cellStatus) {
                model?.currentBlock?.color
            } else {
                Block.getColor(cellStatus as Byte)
            }
            drawCell(canvas, col, row, color as Int)
        }
    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int, rbgColor: Int) {
        paint.color = rbgColor
        val top: Float = (frameOfSet.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val left: Float = (frameOfSet.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val bottom: Float = (frameOfSet.height + (y + 1) * cellSize.height + BLOCK_OFFSET).toFloat()
        val right: Float = (frameOfSet.width + (x + 1) * cellSize.width + BLOCK_OFFSET).toFloat()
        val rectangle = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawRect(
            frameOfSet.width.toFloat(),
            frameOfSet.height.toFloat(),
            width - frameOfSet.width.toFloat(),
            height - frameOfSet.height.toFloat(), paint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val cellW = (w - 2 * FRAME_OFFSET) / FieldConstans.COLUMN_COUNT.value
        val cellH = (h - 2 * FRAME_OFFSET) / FieldConstans.ROW_COUNT.value
        val n = Math.min(cellW, cellH)
        this.cellSize = Dimensionion(n, n)
        val offsetX = (w - FieldConstans.COLUMN_COUNT.value * n) / 2
        val offsetY = (h - FieldConstans.ROW_COUNT.value * n) / 2
        this.frameOfSet = Dimensionion(offsetX, offsetY)
    }


}