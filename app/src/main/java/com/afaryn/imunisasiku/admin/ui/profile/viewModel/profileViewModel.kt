package com.afaryn.imunisasiku.admin.ui.profile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
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
class profileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):ViewModel() {

    private val _getState = MutableStateFlow<UiState<User>>(UiState.Loading(false))
    val getState =_getState.asStateFlow().asLiveData()

    private val _sendingState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendingState = _sendingState.asStateFlow().asLiveData()

    fun getData(){
        _getState.value = UiState.Loading(true)
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val akun = it.toObject(User::class.java)
                    _getState.value = UiState.Success(akun!!)
                    _getState.value = UiState.Loading(false)
                }
            }.addOnFailureListener {
                _getState.value = UiState.Error(it.message.toString())
                _getState.value = UiState.Loading(false)
            }
    }

    fun sendEditAkun(oldData:User,newData:Map<String,Any>)= CoroutineScope(Dispatchers.IO).launch{
        _sendingState.value=UiState.Loading(true)
        val doc = firestore.collection(Constants.USER_COLLECTION)
            .whereEqualTo("name",oldData.name)
            .whereEqualTo("phone",oldData.phone)
            .whereEqualTo("email",oldData.email)
            .whereEqualTo("jenisKelamin",oldData.jenisKelamin)
            .get()
            .await()
        if(doc.documents.isNotEmpty()){
            for (document in doc){
                try {
                    firestore.collection(Constants.USER_COLLECTION).document(document.id).set(
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

    fun updateData(){

    }



    fun delData(){

    }

    fun logout(){
        auth.signOut()

    }
}