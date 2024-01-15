package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.TambahImunisasi
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.utils.Constants.JENIS_IMUNISASI
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class TambahImnViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel(){
    private val _sendingState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendingState = _sendingState.asStateFlow().asLiveData()
    fun sendImunisasi(jenisImunisasi: JenisImunisasi){
        _sendingState.value=UiState.Loading(true)
        firestore.collection(JENIS_IMUNISASI)
            .add(jenisImunisasi)
            .addOnSuccessListener {
               _sendingState.value=UiState.Loading(false)
                _sendingState.value=UiState.Success("Berhasil Mengirim")
            }
            .addOnFailureListener{
                _sendingState.value = UiState.Loading(false)
                _sendingState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
            }
    }
}