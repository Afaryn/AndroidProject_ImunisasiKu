package com.afaryn.sipeni.utils

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
import com.afaryn.sipeni.R
import com.afaryn.sipeni.model.Imunisasi
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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
    val currentDate = Calendar.getInstance().time
    val diffInMillis = currentDate.time - this.time

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = diffInMillis

    val years = calendar.get(Calendar.YEAR) - 1970
    val months = calendar.get(Calendar.MONTH)
    val days = calendar.get(Calendar.DAY_OF_MONTH) - 1

    return "$years Tahun $months Bulan $days Hari"
}

fun Date.sumOfMonths(): Long {
    val date = this.toInstant().atZone(ZoneId.of("Asia/Jakarta")).toLocalDate()
    val today = LocalDate.now()
    return ChronoUnit.MONTHS.between(date, today)
}

fun parseDateString(dateString: Date): String {
    return try {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        "Tidak ada jadwal"
    }
}

fun stringToDate(date: Date): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
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

fun translateDateToIndonesian(inputString: Date): String {
    return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(inputString)
}

fun isSameDay(date: Date): Boolean {
    val today = Date()

    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()

    cal1.time = date
    cal2.time = today

    return cal1.time == cal2.time
}

fun getClosestDate(listImunisasi: List<Imunisasi>): Imunisasi? {
    val currentDate = Date()
    var closestImunisasi: Imunisasi? = null
    var minDifference = Long.MAX_VALUE

    for (imunisasi in listImunisasi) {
        imunisasi.jadwalImunisasi?.let {
            val difference = abs(currentDate.time - imunisasi.jadwalImunisasi.time)

            if (difference < minDifference) {
                minDifference = difference
                closestImunisasi = imunisasi
            }
        }
    }

    return closestImunisasi
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