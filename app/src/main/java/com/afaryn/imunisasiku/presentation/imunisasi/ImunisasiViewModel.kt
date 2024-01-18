package com.afaryn.imunisasiku.presentation.imunisasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.Constants.JENIS_IMUNISASI
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImunisasiViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {
    private val _imunisasiListState = MutableStateFlow<UiState<List<JenisImunisasi>>>(UiState.Loading(false))
    val imunisasiListState = _imunisasiListState.asStateFlow().asLiveData()

    init {
        getJenisImunisasi()
    }

    private fun getJenisImunisasi() {
        firestore.collection(JENIS_IMUNISASI).addSnapshotListener { value, error ->
            if (error != null) {
                _imunisasiListState.value = UiState.Loading(false)
                _imunisasiListState.value = UiState.Error(error.message ?: "Terjadi Kesalahan")
                return@addSnapshotListener
            }

            value?.let {
                _imunisasiListState.value = UiState.Loading(false)
                val jenisImunisasi = value.toObjects(JenisImunisasi::class.java)
                _imunisasiListState.value = UiState.Success(jenisImunisasi)
            }
        }
    }
}