package com.example.tetris.helper

fun arrayOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray> =
    Array(sizeOuter){ ByteArray(sizeInner)}