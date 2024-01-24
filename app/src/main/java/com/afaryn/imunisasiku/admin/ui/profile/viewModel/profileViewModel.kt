package com.afaryn.imunisasiku.admin.ui.profile.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class profileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
):ViewModel() {

    private val _getState = MutableStateFlow<UiState<User>>(UiState.Loading(false))
    val getState =_getState.asStateFlow().asLiveData()

    private val _sendingState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val sendingState = _sendingState.asStateFlow().asLiveData()

    private val _uploadProfileState  = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val uploadProfileState = _uploadProfileState.asStateFlow().asLiveData()

    private val _deleteAccountState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val deleteAccountState = _deleteAccountState.asStateFlow().asLiveData()

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

    fun gantiFotoProfil(user: User, photo: ByteArray) {
        var image = ""
        viewModelScope.launch {
            try {
                async {
                    launch {
                        val imageStorage = storage.reference.child("user/images/${auth.uid}")
                        val result = imageStorage.putBytes(photo).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        image = downloadUrl
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("ProfilViewModel", e.message ?: "Terjadi Kesalahan")
                _uploadProfileState.value = UiState.Error("Terjadi kesalahan saat upload foto ke server")
            }

            val userUpdated = user.copy(
                profile = image
            )
            updateUser(userUpdated)
        }
    }

    private fun updateUser(userUpdated: User) {
        firestore.collection(USER_COLLECTION).document(auth.uid!!).set(userUpdated)
            .addOnFailureListener {
                Log.e("ProfilViewModel", it.message ?: "Terjadi Kesalahan")
                _uploadProfileState.value = UiState.Error("Terjadi kesalahan saat upload foto ke server")
            }
    }

    fun deleteUserData() {
        _deleteAccountState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).delete()
            .addOnSuccessListener {
                deleteAccount()
            }
            .addOnFailureListener {
                _deleteAccountState.value = UiState.Loading(false)

                it.printStackTrace()
                _deleteAccountState.value = UiState.Error("Terjadi kesalahan pada server, silahkan coba lagi nanti")
            }
    }

    private fun deleteAccount() {
        auth.currentUser!!.delete()
            .addOnSuccessListener {
                _deleteAccountState.value = UiState.Loading(false)
                _deleteAccountState.value = UiState.Success("Akun dihapus")
            }
            .addOnFailureListener {
                _deleteAccountState.value = UiState.Loading(false)

                it.printStackTrace()
                _deleteAccountState.value = UiState.Error("Terjadi kesalahan pada server, silahkan coba lagi nanti")
            }
    }

    fun logout(){
        auth.signOut()
    }
}