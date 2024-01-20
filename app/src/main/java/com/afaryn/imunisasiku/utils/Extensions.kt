package com.afaryn.imunisasiku.utils

import android.app.Activity
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun validateEmail(email: String): Validation {
    if (email.isEmpty()) {
        return Validation.Failed("Email tidak boleh kosong")
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return Validation.Failed("Harap isi dengan email yang valid")
    }

    return Validation.Success
}

fun validatePassword(password: String, passwordConfirmation: String?): Validation {
    if (password.isEmpty()) {
        return Validation.Failed("Password tidak boleh kosong")
    }

    if (passwordConfirmation != null) {
        if (password != passwordConfirmation) {
            return Validation.Failed("Password tidak sesuai")
        }
    }

    if (password.length < 6) {
        return Validation.Failed("Password harus terdiri dari 6 huruf atau lebih")
    }

    return Validation.Success
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Date.toToday(): String {
    val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss O yyyy")

    val zoneOffset = ZoneOffset.ofHours(6)
    val startDate = OffsetDateTime.parse(this.toString(), formatter).withOffsetSameLocal(zoneOffset)
    val endDate = OffsetDateTime.parse(Date().toString(), formatter).withOffsetSameLocal(zoneOffset)
    val period = Period.between(startDate.toLocalDate(), endDate.toLocalDate())

    return "${period.years} Tahun ${period.months} Bulan ${period.days} Hari"
}

fun getDayOfWeek(day: String): Int {
    val daysOfWeek = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    )

    // Notifikasi h-1. mengembalikan index di hari sebelumnya
    return when {
        day.contains("Senin") -> {
            daysOfWeek.indexOf("Sunday")
        }
        day.contains("Selasa") -> {
            daysOfWeek.indexOf("Monday")
        }
        day.contains("Rabu") -> {
            daysOfWeek.indexOf("Tuesday")
        }
        day.contains("Kamis") -> {
            daysOfWeek.indexOf("Wednesday")
        }
        day.contains("Jumat") || day.contains("Jum'at") -> {
            daysOfWeek.indexOf("Thursday")
        }
        day.contains("Sabtu") -> {
            daysOfWeek.indexOf("Friday")
        }
        day.contains("Minggu") -> {
            daysOfWeek.indexOf("Saturday")
        }

        else -> {-1}
    }
}

fun parseStringToDate(dateString: String): String {
    val dateParse = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
    return try {
        val formatted = dateParse.parse(dateString)
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(formatted ?: "01 January 1997")
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}