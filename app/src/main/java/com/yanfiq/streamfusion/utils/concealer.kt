package com.yanfiq.streamfusion.utils

// Masking function to obfuscate sensitive information
fun String.mask(
    maskString: String = "*",
    maskAfterLetters: Int = 3,
    isFixSize: Boolean = true,
    maxSize: Int = 10,
    selector: String = "."
): String {
    if(maskAfterLetters < 0) throw RuntimeException("Invalid masking configuration - maskAfterLetters should be greater than 0")
    if(isFixSize && maxSize <= maskAfterLetters) throw RuntimeException("Invalid masking configuration - maxSize must be greater than maskAfterLetters")
    val text = if(isFixSize && length >= maxSize) substring(0, maxSize) else this
    val unmaskLength = if(maskAfterLetters <= length) maskAfterLetters else length
    return text.substring(0, unmaskLength) + text.substring(unmaskLength, text.length).replace(selector.toRegex(), maskString)
}