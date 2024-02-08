package com.afaryn.sipeni.presentation.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.sipeni.model.User
import com.afaryn.sipeni.utils.Constants.USER_COLLECTION
import com.afaryn.sipeni.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
): ViewModel() {
    private val _profileState = MutableStateFlow<UiState<User>>(UiState.Loading(false))
    val profileState = _profileState.asStateFlow().asLiveData()
    private val _uploadProfileState  = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val uploadProfileState = _profileState.asStateFlow().asLiveData()

    init {
        getUser()
    }

    private fun getUser() {
        _profileState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).addSnapshotListener { value, error ->
            if (error != null) {
                _profileState.value = UiState.Loading(false)
                _profileState.value = UiState.Error(error.localizedMessage ?: "Terjadi Kesalahan")
                return@addSnapshotListener
            }

            val data = value?.toObject(User::class.java)
            data?.let {
                _profileState.value = UiState.Loading(false)
                _profileState.value = UiState.Success(it)
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
}