package com.afaryn.imunisasiku.model

data class JenisImunisasi(
    val namaImunisasi: String? = null,
    val batasUmur : Int? = null,
    val jadwalImunisasi: ArrayList<String>?=null,
    val jamImunisasi: String? = null
)