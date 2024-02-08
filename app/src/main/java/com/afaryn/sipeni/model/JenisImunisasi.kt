package com.afaryn.sipeni.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList
import java.util.Date

@Parcelize
data class JenisImunisasi(
    val namaImunisasi: String? = null,
    val batasUmur : Int? = null,
    val jadwalImunisasi: ArrayList<Date>?=null,
    val jamImunisasi: ArrayList<String>?=null,
): Parcelable