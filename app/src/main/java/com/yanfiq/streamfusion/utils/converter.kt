package com.yanfiq.streamfusion.utils

fun textToBinary(text: String): String {
    return text.map {
        // Convert each character to its ASCII value and then to a binary string
        it.code.toString(2).padStart(8, '0')
    }.joinToString("")
}

fun binaryToText(binary: String): String {
    return binary.chunked(8) // Split the binary string into chunks of 8 bits
        .map { it.toInt(2).toChar() } // Convert each chunk to a character
        .joinToString("") // Join the characters into a string
}