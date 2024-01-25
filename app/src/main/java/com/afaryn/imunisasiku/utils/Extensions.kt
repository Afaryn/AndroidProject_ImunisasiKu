package com.afaryn.imunisasiku.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.model.Imunisasi
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

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
        day.replaceFirstChar { it.uppercaseChar() }.contains("Senin") -> {
            daysOfWeek.indexOf("Monday")
        }

        day.replaceFirstChar { it.uppercaseChar() }.contains("Selasa") -> {
            daysOfWeek.indexOf("Tuesday")
        }

        day.replaceFirstChar { it.uppercaseChar() }.contains("Rabu") -> {
            daysOfWeek.indexOf("Wednesday")
        }

        day.replaceFirstChar { it.uppercaseChar() }.contains("Kamis") -> {
            daysOfWeek.indexOf("Thursday")
        }

        day.replaceFirstChar { it.uppercaseChar() }
            .contains("Jumat") || day.replaceFirstChar { it.uppercaseChar() }
            .contains("Jum'at") -> {
            daysOfWeek.indexOf("Friday")
        }

        day.replaceFirstChar { it.uppercaseChar() }.contains("Sabtu") -> {
            daysOfWeek.indexOf("Saturday")
        }

        day.replaceFirstChar { it.uppercaseChar() }.contains("Minggu") -> {
            daysOfWeek.indexOf("Sunday")
        }

        else -> {
            -1
        }
    }
}

fun parseDateString(dateString: String): String {
    val dateParse = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
    return try {
        val formatted = dateParse.parse(dateString)
        DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy").format(formatted)
    } catch (e: Exception) {
        e.printStackTrace()
        "Tidak ada jadwal"
    }
}

fun stringToDate(dateString: String): Calendar {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
    val date = LocalDate.parse(dateString, formatter)
    val calendar = Calendar.getInstance()
    calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
    return calendar
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

fun isSameDay(dateString: String): Boolean {
    val stringToDate = stringToDate(dateString)
    val today = Date()

    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()

    cal1.time = stringToDate.time
    cal2.time = today

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

fun calendarToString(calendar: Calendar): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy").withZone(ZoneId.systemDefault())
    return formatter.format(calendar.toInstant())
}

fun getClosestDate(dateObjects: List<String>, listImunisasi: List<Imunisasi>): Imunisasi? {
    val objects = dateObjects.map { stringToDate(it) }
    val dates = objects.minByOrNull { abs((it.timeInMillis) - Calendar.getInstance().timeInMillis) }
    if (dates != null) {
        val dateString = calendarToString(dates)
        val imunisasiTerdekat = listImunisasi.filter { it.jadwalImunisasi == dateString }
        imunisasiTerdekat[0].let {
            return it
        }
    } else {
        return null
    }
}

fun ImageView.glide(url: String) {
    Glide.with(this.context).load(url).into(this)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())
fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

private const val MAXIMAL_SIZE = 1000000
fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}