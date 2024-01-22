package com.afaryn.imunisasiku.presentation.imunisasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.Constants.IMUNISASI_COLLECTION
import com.afaryn.imunisasiku.utils.Constants.JENIS_IMUNISASI
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DaftarImunisasiViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _imunisasiListState = MutableStateFlow<UiState<List<JenisImunisasi>>>(UiState.Loading(false))
    val imunisasiListState = _imunisasiListState.asStateFlow().asLiveData()
    private val _daftarImunisasiState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val daftarImunisasiState = _daftarImunisasiState.asStateFlow().asLiveData()

    init {
        getJenisImunisasi()
    }

    private fun getJenisImunisasi() {
        _imunisasiListState.value = UiState.Loading(true)
        firestore.collection(JENIS_IMUNISASI).addSnapshotListener { value, error ->
            if (error != null) {
                _imunisasiListState.value = UiState.Loading(false)
                _imunisasiListState.value = UiState.Error(error.localizedMessage ?: "Terjadi Kesalahan")
                return@addSnapshotListener
            }

            value?.let {
                _imunisasiListState.value = UiState.Loading(false)
                val jenisImunisasi = value.toObjects(JenisImunisasi::class.java)
                _imunisasiListState.value = UiState.Success(jenisImunisasi)
            }
        }
    }

    fun daftarImunisasi(imunisasi: Imunisasi) {
        _daftarImunisasiState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(IMUNISASI_COLLECTION).document(imunisasi.id).set(imunisasi)
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                IMUNISASI_COLLECTION).document(imunisasi.id).set(imunisasi)
        }.addOnSuccessListener {
            _daftarImunisasiState.value = UiState.Loading(false)
            _daftarImunisasiState.value = UiState.Success("Berhasil daftar imunisasi ${imunisasi.namaImunisasi}")
        }.addOnFailureListener {
            _daftarImunisasiState.value = UiState.Loading(false)
            _daftarImunisasiState.value = UiState.Error(it.localizedMessage ?: "Terjadi Kesalahan")
        }
    }
}