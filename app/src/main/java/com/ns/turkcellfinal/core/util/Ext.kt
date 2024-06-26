package com.ns.turkcellfinal.core.util

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDate(): String {
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME

    val date = ZonedDateTime.parse(this, inputFormatter)

    val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    return date.format(outputFormatter)
}


fun getCurrentDate(): String {
    val date = Date()
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
    return formatter.format(date)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showSnackbar(text: String) {
    val snackbar = Snackbar.make(
        this,
        text,
        Snackbar.LENGTH_LONG
    )
    snackbar.show()
}