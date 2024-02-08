package com.afaryn.sipeni.presentation.imunisasi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.sipeni.model.Imunisasi
import com.afaryn.sipeni.utils.Constants.IMUNISASI_COLLECTION
import com.afaryn.sipeni.utils.Constants.USER_COLLECTION
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.stringToDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ImunisasiKuViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel() {
    private val _imunisasiKuState = MutableStateFlow<UiState<List<Imunisasi>>>(UiState.Loading(false))
    val imunisasiKuState = _imunisasiKuState.asStateFlow().asLiveData()
    private val _cancelImunisasiState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val cancelImunisasiState = _cancelImunisasiState.asStateFlow().asLiveData()

    init {
        getImunisasi()
    }

    private fun getImunisasi() {
        _imunisasiKuState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(IMUNISASI_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _imunisasiKuState.value = UiState.Loading(false)
                    _imunisasiKuState.value = UiState.Error(error.localizedMessage ?: "Terjadi Kesalahan")
                    return@addSnapshotListener
                }

                value?.let {
                    _imunisasiKuState.value = UiState.Loading(false)
                    val imunisasi = value.toObjects(Imunisasi::class.java)
                    val imunisasiFiltered = imunisasi.filter {
                        stringToDate(it.jadwalImunisasi!!).time >= Date() &&
                                it.statusImunisasi != "Dibatalkan"
                    }

                    _imunisasiKuState.value = UiState.Success(imunisasiFiltered)
                }
            }
    }

    fun batalkanImunisasi(imunisasi: Imunisasi) {
        _cancelImunisasiState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(IMUNISASI_COLLECTION).document(imunisasi.id).set(imunisasi)
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(IMUNISASI_COLLECTION)
                .document(imunisasi.id).set(imunisasi)
        }.addOnSuccessListener {
            _cancelImunisasiState.value = UiState.Loading(false)
            _cancelImunisasiState.value = UiState.Success("Imunisasi dibatalkan")
        }.addOnFailureListener {
            _cancelImunisasiState.value = UiState.Loading(false)
            _cancelImunisasiState.value = UiState.Error(it.localizedMessage ?: "Terjadi Kesalahan")
        }
    }
}