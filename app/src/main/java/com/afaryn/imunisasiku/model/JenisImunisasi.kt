package com.afaryn.imunisasiku.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
data class JenisImunisasi(
    val namaImunisasi: String? = null,
    val batasUmur : Int? = null,
    val jadwalImunisasi: ArrayList<String>?=null,
    val jamImunisasi: ArrayList<String>?=null,
    val siklus:String?=null
): Parcelable