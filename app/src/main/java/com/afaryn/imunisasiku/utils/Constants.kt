package com.afaryn.imunisasiku.utils

import android.Manifest

object Constants {
    const val USER_COLLECTION = "user"
    const val PASIEN_COLLECTION = "pasien"
    const val JENIS_IMUNISASI = "JenisImunisasi"
    const val PICK_PASIEN = "pick_pasien"
    const val REQUEST_PICK_PASIEN = 101
    const val CYCLE_MONTHLY = "bulan"
    const val IMUNISASI_COLLECTION = "imunisasi"
    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}