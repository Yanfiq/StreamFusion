package com.yanfiq.streamfusion.utils

import java.util.regex.Pattern

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

fun ISODurationToSeconds(duration: String): Int {
    val pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
    val matcher = pattern.matcher(duration)

    var totalSeconds = 0

    if (matcher.matches()) {
        val hours = matcher.group(1)?.toIntOrNull() ?: 0
        val minutes = matcher.group(2)?.toIntOrNull() ?: 0
        val seconds = matcher.group(3)?.toIntOrNull() ?: 0

        totalSeconds = hours * 3600 + minutes * 60 + seconds
    }

    return totalSeconds
}