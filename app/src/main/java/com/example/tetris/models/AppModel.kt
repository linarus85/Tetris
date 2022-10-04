package com.example.tetris.models

import android.graphics.Point
import com.example.tetris.enums.FieldConstans
import com.example.tetris.helper.arrayOfByte
import com.example.tetris.storage.AppPreferrences

class AppModel {
    var score: Int = 0
    private var preferences: AppPreferrences? = null
    var currentBlock: Block? = null
    var currentState: String = Statuses.AWAITING_START.name

    enum class Statuses {
        AWAITING_START,
        ACTIVE,
        INACTIVE,
        OVER
    }

    enum class Motion {
        LEFT,
        RIGHT,
        DOWN,
        ROTATE
    }

    enum class CellConstants(val value: Byte) {
        EMPTY(0),
        EPEMERAL(1)
    }

    private var field: Array<ByteArray> = arrayOfByte(
        FieldConstans.ROW_COUNT.value,
        FieldConstans.COLUMN_COUNT.value
    )

    fun setPreferences(preferences: AppPreferrences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun isGameOwer(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    private fun boostScore() {
        score += 1
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConstans.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstans.COLUMN_COUNT.value) {
            false
        } else {
            for (i in 0 until shape.size) {
                for (j in 0 until shape[i].size) {
                    val y = position.y + i
                    val x = position.x + j
                    if (CellConstants.EMPTY.value != shape[i][j] &&
                        CellConstants.EMPTY.value != field[y][x]
                    ) {
                        return false
                    }
                }
            }
            true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                Motion.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }
                Motion.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }
                Motion.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }
                Motion.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(
                    currentBlock?.position as Point,
                    currentBlock?.frameNumber as Int
                )
                if (Motion.DOWN.name == action) {
                    boostScore()
                    persistSellData()
                    assetsField()
                    generateNextBlock()
                    if (!blockAddPosible()) {
                        currentState = Statuses.OVER.name;
                        currentBlock = null;
                        resetField(false);
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun blockAddPosible(): Boolean {
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)) {
            return false
        }
        return true
    }

    private fun assetsField() {
        for (i in 0 until field.size) {
            var emptySize = 0
            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty) emptySize++
            }
            if (emptySize == 0) {
                shiftRows(i)
            }
        }
    }

    private fun shiftRows(i: Int) {
        if (i > 0) {
            for (j in i - 1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for (j in 0 until field[0].size) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    private fun persistSellData() {
        for (i in 0 until field.size) {
            for (j in 0 until field[i].size) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPEMERAL.value) {
                    status = currentBlock?.staticValue
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null) {
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j]) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun resetField(ephemeralCellOnly: Boolean = true) {
        for (i in 0 until FieldConstans.ROW_COUNT.value) {
            (0 until FieldConstans.COLUMN_COUNT.value).filter {
                !ephemeralCellOnly || field[i][it] == CellConstants.EPEMERAL.value
            }.forEach { field[i][it] = CellConstants.EMPTY.value }
        }
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }


    fun endGame() {
        score = 0
        currentState = Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }


}