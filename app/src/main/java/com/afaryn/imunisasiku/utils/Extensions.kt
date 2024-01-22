package com.afaryn.imunisasiku.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afaryn.imunisasiku.R
import java.text.SimpleDateFormat
import java.time.LocalDate
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
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(formatted ?: "01 January 1997")
    } catch (e: Exception) {
        e.printStackTrace()
        "Tidak ada jadwal"
    }
}

@SuppressLint("InflateParams", "MissingInflatedId")
fun Activity.setupDeleteDialog(
    title: String,
    message: String,
    btnActionText: String,
    onYesClick: () -> Unit
) {
    val dialog = Dialog(this, android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.delete_pasien_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
    val tvMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
    tvTitle.text = title
    tvMessage.text = message

    val btnDismiss = view.findViewById<Button>(R.id.btn_dialog_dismiss)
    val btnYes = view.findViewById<Button>(R.id.btn_dialog_yes)
    btnYes.text = btnActionText

    btnDismiss.setOnClickListener {
        dialog.dismiss()
    }
    btnYes.setOnClickListener {
        onYesClick()
        dialog.dismiss()
    }
}

fun translateDateToIndonesian(inputString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.US)
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))

    return LocalDate.parse(inputString, inputFormatter).format(outputFormatter)
}