package com.afaryn.imunisasiku.model

import java.util.ArrayList

data class JenisImunisasi(
    val namaImunisasi: String? = null,
    val batasUmur : Int? = null,
    val jadwalImunisasi: ArrayList<String>?=null,
    val jamImunisasi: ArrayList<String>?=null,
    val siklus:String?=null
)