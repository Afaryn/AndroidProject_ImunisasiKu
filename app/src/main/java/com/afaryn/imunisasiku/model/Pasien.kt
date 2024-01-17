package com.afaryn.imunisasiku.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class Pasien(
    val id: String = Random.nextLong(0, 1_000_000).toString() + SimpleDateFormat(
        "yyyyMMdd",
        Locale.ENGLISH
    ).format(Date()),
    val name: String? = null,
    val jenisKelamin: String? = null,
    val tanggalLahir: Date? = null,
    val nik: String? = null,
    val catatan: String? = null
)