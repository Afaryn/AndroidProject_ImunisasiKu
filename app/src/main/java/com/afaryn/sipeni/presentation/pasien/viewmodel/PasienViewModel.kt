package com.afaryn.sipeni.presentation.pasien.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.sipeni.model.Pasien
import com.afaryn.sipeni.utils.Constants.PASIEN_COLLECTION
import com.afaryn.sipeni.utils.Constants.USER_COLLECTION
import com.afaryn.sipeni.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PasienViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _addPatientState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val addPatientState = _addPatientState.asStateFlow().asLiveData()
    private val _patientState = MutableStateFlow<UiState<List<Pasien>>>(UiState.Loading(false))
    val patientState = _patientState.asStateFlow().asLiveData()
    private val _deletePatientState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val deletePatientState = _deletePatientState.asStateFlow().asLiveData()

    fun addPatient(pasien: Pasien) {
        _addPatientState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(PASIEN_COLLECTION)
                .document(pasien.id).set(pasien)
            firestore.collection(PASIEN_COLLECTION).document(pasien.id).set(pasien)
        }.addOnSuccessListener {
                _addPatientState.value = UiState.Loading(false)
                _addPatientState.value = UiState.Success("Berhasil menambah pasien")
        }.addOnFailureListener {
            _addPatientState.value = UiState.Loading(false)
            _addPatientState.value = UiState.Error(it.localizedMessage ?: "Terjadi Kesalahan")
        }
    }

    fun getAllPasien() {
        _patientState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(PASIEN_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _patientState.value = UiState.Loading(false)
                    _patientState.value = UiState.Error(error.localizedMessage ?: "Terjadi Kesalahan")
                    return@addSnapshotListener
                }

                value?.let {
                    _patientState.value = UiState.Loading(false)
                    val pasien = value.toObjects(Pasien::class.java)
                    _patientState.value = UiState.Success(pasien)
                }
            }
    }

    fun deletePasien(pasienId: String) {
        _deletePatientState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(PASIEN_COLLECTION)
                .document(pasienId).delete()
            firestore.collection(PASIEN_COLLECTION).document(pasienId).delete()
        }.addOnSuccessListener {
            _deletePatientState.value = UiState.Loading(false)
            _deletePatientState.value = UiState.Success("Data pasien dihapus")
        }.addOnFailureListener {
            _deletePatientState.value = UiState.Loading(false)
            _deletePatientState.value = UiState.Error(it.localizedMessage ?: "Terjadi Kesalahan")
        }
    }
}