package com.afaryn.imunisasiku.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Parcelize
data class Imunisasi(
    val id: String = Random.nextLong(0, 1_000_000).toString() + SimpleDateFormat(
        "yyyyMMdd",
        Locale.ENGLISH
    ).format(Date()),
    val pasien: Pasien? = null,
    val namaImunisasi: String? = null,
    val jadwalImunisasi: Date? = null,
    val jamImunisasi: String? = null
): Parcelable
