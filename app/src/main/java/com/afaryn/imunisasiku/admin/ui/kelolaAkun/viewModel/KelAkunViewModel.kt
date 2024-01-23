package com.afaryn.imunisasiku.admin.ui.kelolaAkun.viewModel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
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
class KelAkunViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
):ViewModel() {

    private val _sendingState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendingState =_sendingState.asStateFlow().asLiveData()

    private val _getDataState = MutableStateFlow<UiState<List<User>>>(UiState.Loading(false))
    val getDataState = _getDataState.asStateFlow().asLiveData()

    private val _delState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val delState = _delState.asStateFlow().asLiveData()



    fun sendEditAkun(oldData:User,newData:Map<String,Any>)= CoroutineScope(Dispatchers.IO).launch{
        _sendingState.value=UiState.Loading(true)
        val doc = firestore.collection(USER_COLLECTION)
                    .whereEqualTo("name",oldData.name)
                    .whereEqualTo("phone",oldData.phone)
                    .whereEqualTo("email",oldData.email)
                    .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(USER_COLLECTION).document(document.id).set(
                        newData, SetOptions.merge()

                    ).addOnSuccessListener {
                        _sendingState.value=UiState.Loading(false)
                        _sendingState.value=UiState.Success("Berhasil memperbarui data!")
                    }

                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        _sendingState.value = UiState.Error(e.toString())
                    }
                }
            }
        }


    }

    fun getAllData(){
        _getDataState.value= UiState.Loading(true)
        firestore.collection(USER_COLLECTION)
            .get()
            .addOnSuccessListener {
                val akun = it.toObjects(User::class.java)
                _getDataState.value = UiState.Success(akun)
                _getDataState.value= UiState.Loading(false)
            }
            .addOnFailureListener{
                _getDataState.value = UiState.Loading(false)
                _getDataState.value = UiState.Error(it.message.toString())
            }
    }

    fun DelImunisasi(item:String)= CoroutineScope(Dispatchers.IO).launch {
        _delState.value=UiState.Loading(true)
        val doc = firestore.collection(USER_COLLECTION)
            .whereEqualTo("email",item)
//            .whereEqualTo("siklus",item.siklus)
//            .whereEqualTo("bataUmur",item.batasUmur)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(USER_COLLECTION).document(document.id).delete()

                        .addOnSuccessListener {
                            _delState.value=UiState.Loading(false)
                            _delState.value=UiState.Success("Berhasil menghapus akun ${item}!")
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