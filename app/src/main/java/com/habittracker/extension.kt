package com.habittracker

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDate(pattern: String): String {
    val initialFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    val date = initialFormat.parse(this)
    return format.format(date).toString()
}

fun String.customFormat(patternInit: String, pattern: String): String {
    val initialFormat = SimpleDateFormat(patternInit, Locale.ENGLISH)
    val formatOutput = SimpleDateFormat(pattern, Locale("in", "ID"))
    val date = initialFormat.parse(this)
    return formatOutput.format(date).toString()
}