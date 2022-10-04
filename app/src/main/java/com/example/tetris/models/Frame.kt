package com.example.tetris.models

import com.example.tetris.helper.arrayOfByte

class Frame(private val width: Int) {
    val data: ArrayList<ByteArray> = ArrayList()
    fun addRow(byteStr: String): Frame {
        val row  = ByteArray(byteStr.length)
        for (i in byteStr.indices){
            row[i] = byteStr[i].toString().toByte()
        }
        data.add(row)
        return this
    }
    fun getByteArray():Array<ByteArray>{
        val bytes = arrayOfByte(data.size,width)
        return data.toArray(bytes)
    }
}