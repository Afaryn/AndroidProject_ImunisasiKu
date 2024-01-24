package com.afaryn.imunisasiku.admin.ui.kelolaImunisasi.viewModel


import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.afaryn.imunisasiku.model.JenisImunisasi

import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.utils.Constants

import com.afaryn.imunisasiku.utils.Constants.JENIS_IMUNISASI

import com.afaryn.imunisasiku.utils.UiState


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class TambahImnViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel(){
    private val _sendingState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendingState = _sendingState.asStateFlow().asLiveData()

    private val _delState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val delState = _delState.asStateFlow().asLiveData()

    private val _getDataState = MutableStateFlow<UiState<List<JenisImunisasi>>>(UiState.Loading(false))
    val getDataState = _getDataState.asStateFlow().asLiveData()

    fun sendImunisasi(jenisImunisasi: JenisImunisasi) = CoroutineScope(Dispatchers.IO).launch {
        _sendingState.value=UiState.Loading(true)
        val doc = firestore.collection(JENIS_IMUNISASI)
            .whereEqualTo("namaImunisasi",jenisImunisasi.namaImunisasi)
            .get()
            .await()
        if(doc.documents.isEmpty()){
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
        }else{
            _sendingState.value = UiState.Loading(false)
            _sendingState.value = UiState.Error("Jenis Imunisasi sudah ada")
        }

    }

//    fun updateImn(oldData:JenisImunisasi,newData:JenisImunisasi){
//        _sendingState.value=UiState.Loading(true)
//        val cekData = firestore.collection(JENIS_IMUNISASI)
//            .whereEqualTo("namaImunisasi",oldData.namaImunisasi)
//            .whereEqualTo("batasUmur",oldData.batasUmur)
//            .
//        firestore.collection(JENIS_IMUNISASI)
//
//            .addOnSuccessListener {
//                _sendingState.value=UiState.Loading(false)
//                _sendingState.value=UiState.Success("Berhasil Mengirim")
//            }
//            .addOnFailureListener{
//                _sendingState.value = UiState.Loading(false)
//                _sendingState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
//            }
//    }

    fun getAllData(){
        _getDataState.value = UiState.Loading(true)
        firestore.collection(JENIS_IMUNISASI)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(JenisImunisasi::class.java)
                _getDataState.value = UiState.Success(orders)
                _getDataState.value = UiState.Loading(false)
            }
            .addOnFailureListener {
                _getDataState.value = UiState.Error(it.message.toString())
                _getDataState.value = UiState.Loading(false)
            }
    }

    fun DelImunisasi(item:String)= CoroutineScope(Dispatchers.IO).launch {
        _delState.value=UiState.Loading(true)
        val doc = firestore.collection(JENIS_IMUNISASI)
            .whereEqualTo("namaImunisasi",item)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(JENIS_IMUNISASI).document(document.id).delete()

                    .addOnSuccessListener {
                        _delState.value=UiState.Loading(false)
                        _delState.value=UiState.Success("Berhasil menghapus Imunisasi ${item}!")
                    }

                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        _delState.value=UiState.Loading(false)
                        _delState.value = UiState.Error(e.toString())
                    }
                }
            }
        }
    }
}