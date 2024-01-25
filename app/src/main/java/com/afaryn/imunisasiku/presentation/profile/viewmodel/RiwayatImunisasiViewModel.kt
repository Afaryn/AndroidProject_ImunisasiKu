package com.afaryn.imunisasiku.presentation.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RiwayatImunisasiViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _imunisasiState = MutableStateFlow<UiState<List<Imunisasi>>>(UiState.Loading(false))
    val imunisasiState = _imunisasiState.asStateFlow().asLiveData()

    init {
        getImunisasi()
    }

    private fun getImunisasi() {
        _imunisasiState.value = UiState.Loading(true)
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(Constants.IMUNISASI_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _imunisasiState.value = UiState.Loading(false)

                    error.printStackTrace()
                    _imunisasiState.value = UiState.Error("Terjadi Kesalahan saat mencoba terhubung ke server")
                    return@addSnapshotListener
                }

                value?.let {
                    _imunisasiState.value = UiState.Loading(false)
                    val imunisasi = value.toObjects(Imunisasi::class.java)
                    _imunisasiState.value = UiState.Success(imunisasi)
                }
            }
    }
}