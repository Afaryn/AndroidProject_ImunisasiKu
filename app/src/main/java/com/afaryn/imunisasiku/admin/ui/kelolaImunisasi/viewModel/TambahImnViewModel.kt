package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class TambahImnViewModel @Inject constructor(private val firestore: FirebaseFirestore):ViewModel(){
    private var _isLoading = MutableLiveData<String>()
    val isLoading : LiveData<String>
        get() = _isLoading
    fun sendImunisasi(jenisImunisasi: JenisImunisasi){
        firestore.collection("JenisImunisasi")
            .add(jenisImunisasi)
            .addOnSuccessListener {
                //handle proses sukses
                _isLoading.value="berhasil mengirimkan"
            }
            .addOnFailureListener{
                //handle proses gagal
                _isLoading.value="gagal mengirimkan"
            }
    }
}