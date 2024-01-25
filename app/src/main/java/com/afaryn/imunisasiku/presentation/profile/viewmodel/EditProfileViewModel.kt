package com.afaryn.imunisasiku.presentation.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _editProfileState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val editProfileState = _editProfileState.asStateFlow().asLiveData()
    private val _userInfoState = MutableStateFlow<UiState<User>>(UiState.Loading(false))
    val userInfoState = _userInfoState.asStateFlow().asLiveData()
    private val _deleteAccountState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val deleteAccountState = _deleteAccountState.asStateFlow().asLiveData()

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        _userInfoState.value = UiState.Loading(false)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).get()
            .addOnSuccessListener {
                _userInfoState.value = UiState.Loading(false)

                val user = it.toObject<User>()
                user?.let { _userInfoState.value = UiState.Success(user) }
            }
            .addOnFailureListener {
                _userInfoState.value = UiState.Loading(false)

                Log.e("EditProfileViewModel", it.message ?: "Terjadi kesalahan")
                it.printStackTrace()
                _userInfoState.value = UiState.Error("Terjadi kesalahan pada server")
            }
    }

    fun editProfile(user: User) {
        _editProfileState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).set(user)
            .addOnSuccessListener {
                _editProfileState.value = UiState.Loading(false)
                _editProfileState.value = UiState.Success("Berhasil mengubah profil")
            }
            .addOnFailureListener {
                _editProfileState.value = UiState.Loading(false)

                Log.e("EditProfileViewModel", it.message ?: "Terjadi kesalahan")
                it.printStackTrace()
                _editProfileState.value = UiState.Error("Terjadi kesalahan pada server")
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

    fun signOut() {
        auth.signOut()
    }
}