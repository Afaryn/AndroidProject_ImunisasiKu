package com.afaryn.imunisasiku.utils

import android.Manifest

object Constants {
    const val USER_COLLECTION = "user"
    const val PASIEN_COLLECTION = "pasien"
    const val JENIS_IMUNISASI = "JenisImunisasi"
    const val PICK_PASIEN = "pick_pasien"
    const val REQUEST_PICK_PASIEN = 101
    const val IMUNISASI_COLLECTION = "imunisasi"
    const val PENTINGNYA_IMUNISASI_URI = "https://firebasestorage.googleapis.com/v0/b/imunisasiku-e3356.appspot.com/o/pentingnyaimunisasi%2Fpentingnya_imunisasi.mp4?alt=media&token=2218defc-580b-4d3b-bb56-54970f114e8c"
    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}