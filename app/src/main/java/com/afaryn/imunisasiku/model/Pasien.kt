package com.afaryn.imunisasiku.model

import java.util.Date

data class Pasien(
    val name: String? = null,
    val jenisKelamin: String? = null,
    val tanggalLahir: Date? = null,
    val usia: Int? = Date().year - tanggalLahir!!.year,
    val nik: Int? = null,
    val catatan: String? = null
)