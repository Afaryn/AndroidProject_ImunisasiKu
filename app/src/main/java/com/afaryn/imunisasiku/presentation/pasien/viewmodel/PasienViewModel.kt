package com.afaryn.imunisasiku.presentation.pasien.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.Pasien
import com.afaryn.imunisasiku.utils.Constants.PASIEN_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PasienViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _addPatientState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val addPatientState = _addPatientState.asStateFlow().asLiveData()
    private val _patientState = MutableStateFlow<UiState<List<Pasien>>>(UiState.Loading(false))
    val patientState = _patientState.asStateFlow().asLiveData()

    fun addPatient(pasien: Pasien) {
        _addPatientState.value = UiState.Loading(true)
        firestore.collection(PASIEN_COLLECTION).document(auth.uid!!).collection(PASIEN_COLLECTION)
            .document(
                pasien.id
            ).set(pasien)
            .addOnSuccessListener {
                _addPatientState.value = UiState.Loading(false)
                _addPatientState.value = UiState.Success("Berhasil menambah pasien")
            }
            .addOnFailureListener {
                _addPatientState.value = UiState.Loading(false)
                _addPatientState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
            }
    }

    fun getAllPasien() {
        _patientState.value = UiState.Loading(true)
        firestore.collection(PASIEN_COLLECTION).document(auth.uid!!).collection(PASIEN_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _patientState.value = UiState.Loading(false)
                    _patientState.value = UiState.Error(error.message ?: "Terjadi Kesalahan")
                    return@addSnapshotListener
                }

                value?.let {
                    _patientState.value = UiState.Loading(false)
                    val pasien = value.toObjects(Pasien::class.java)
                    _patientState.value = UiState.Success(pasien)
                }
            }
    }
}