package com.afaryn.sipeni.admin.ui.jadwal.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.sipeni.model.Imunisasi
import com.afaryn.sipeni.model.JenisImunisasi
import com.afaryn.sipeni.utils.Constants
import com.afaryn.sipeni.utils.Constants.IMUNISASI_COLLECTION
import com.afaryn.sipeni.utils.Constants.JENIS_IMUNISASI
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.stringToDate
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class jadwalViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel() {

    private val _getState = MutableStateFlow<UiState<List<JenisImunisasi>>>(UiState.Loading(false))
    val getState = _getState.asStateFlow().asLiveData()

    private val _getSiklusState = MutableStateFlow<UiState<JenisImunisasi>>(UiState.Loading(false))
    val getSiklusState = _getSiklusState.asStateFlow().asLiveData()

    private val _sendState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendState = _sendState.asStateFlow().asLiveData()

    private val _getImnState = MutableStateFlow<UiState<List<Imunisasi>>>(UiState.Loading(false))
    val getImnState = _getImnState.asStateFlow().asLiveData()

    private val _cancelImunisasiState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val cancelImunisasiState = _cancelImunisasiState.asStateFlow().asLiveData()

    fun getJadwal() {
        _getImnState.value = UiState.Loading(true)
        firestore.collection(IMUNISASI_COLLECTION).addSnapshotListener { value, error ->
            if (error != null) {
                error.printStackTrace()
                _getImnState.value = UiState.Loading(false)
                _getImnState.value = UiState.Error("Terjadi kesalahan saat mengambil data")
            }

            val data = value?.toObjects(Imunisasi::class.java)
            data?.let {
                val imunisasiFiltered = it.filter { imunisasi ->
                    stringToDate(imunisasi.jadwalImunisasi!!).time >= Date() &&
                            imunisasi.statusImunisasi != "Dibatalkan"
                }
                _getImnState.value = UiState.Loading(false)
                _getImnState.value = UiState.Success(imunisasiFiltered)
            }
        }
    }

    fun cancelImunisasi(imunisasi: Imunisasi) {
        _cancelImunisasiState.value = UiState.Loading(true)
        firestore.runBatch {
            firestore.collection(IMUNISASI_COLLECTION).document(imunisasi.id).set(imunisasi)
            firestore.collection(Constants.USER_COLLECTION).document(imunisasi.userId).collection(IMUNISASI_COLLECTION)
                .document(imunisasi.id).set(imunisasi)
        }.addOnSuccessListener {
            _cancelImunisasiState.value = UiState.Loading(false)
            _cancelImunisasiState.value = UiState.Success("Imunisasi dibatalkan")
        }.addOnFailureListener {
            _cancelImunisasiState.value = UiState.Loading(false)
            _cancelImunisasiState.value = UiState.Error(it.localizedMessage ?: "Terjadi Kesalahan")
        }
    }

    fun getImn(){
        _getState.value = UiState.Loading(true)
        firestore.collection(JENIS_IMUNISASI)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(JenisImunisasi::class.java)
                _getState.value = UiState.Success(orders)
                _getState.value = UiState.Loading(false)
            }
            .addOnFailureListener {
                _getState.value = UiState.Error(it.message.toString())
                _getState.value = UiState.Loading(false)
            }
    }

    fun getSiklus (nama:String)= CoroutineScope(Dispatchers.IO).launch{
        _getSiklusState.value = UiState.Loading(true)
        val doc = firestore.collection(JENIS_IMUNISASI)
            .whereEqualTo("namaImunisasi",nama)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(JENIS_IMUNISASI).document(document.id).get()
                        .addOnSuccessListener {
                        val orders = it.toObject(JenisImunisasi::class.java)
                        _getSiklusState.value = UiState.Success(orders!!)
                        _getSiklusState.value = UiState.Loading(false)
                    }
                        .addOnFailureListener {
                            _getSiklusState.value = UiState.Error(it.message.toString())
                            _getSiklusState.value = UiState.Loading(false)
                        }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        _getSiklusState.value = UiState.Error(e.toString())
                    }
                }
            }
        }
    }

    fun sendHari (namaImunisasi:String,newHari:List<Date>)= CoroutineScope(Dispatchers.IO).launch{
        _sendState.value = UiState.Loading(true)

        val doc = firestore.collection(JENIS_IMUNISASI)
            .whereEqualTo("namaImunisasi",namaImunisasi)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(JENIS_IMUNISASI).document(document.id).update(
                        "jadwalImunisasi", newHari

                    ).addOnSuccessListener {
                        _sendState.value=UiState.Loading(false)
                        _sendState.value=UiState.Success("Berhasil menambah Jadwal Hari!")
                    }

                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        _sendState.value=UiState.Loading(false)
                        _sendState.value = UiState.Error(e.toString())
                    }
                }
            }
        }
    }

    fun sendJam (namaImunisasi:String,newJam:List<String>)= CoroutineScope(Dispatchers.IO).launch{
        _sendState.value = UiState.Loading(true)

        val doc = firestore.collection(JENIS_IMUNISASI)
            .whereEqualTo("namaImunisasi",namaImunisasi)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(JENIS_IMUNISASI).document(document.id).update(
                        "jamImunisasi", newJam

                    ).addOnSuccessListener {
                        _sendState.value=UiState.Loading(false)
                        _sendState.value=UiState.Success("Berhasil menambah Jadwal Jam!")
                    }

                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        _sendState.value=UiState.Loading(false)
                        _sendState.value = UiState.Error(e.toString())
                    }
                }
            }
        }
    }

}