package com.afaryn.sipeni.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.sipeni.model.User
import com.afaryn.sipeni.utils.Constants.USER_COLLECTION
import com.afaryn.sipeni.utils.FieldsState
import com.afaryn.sipeni.utils.UiState
import com.afaryn.sipeni.utils.Validation
import com.afaryn.sipeni.utils.validateEmail
import com.afaryn.sipeni.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val loginState = _loginState.asStateFlow().asLiveData()
    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val registerState = _registerState.asStateFlow().asLiveData()
    private val _validation = Channel<FieldsState>()
    val validation = _validation.receiveAsFlow()

    fun login(email: String, password: String, loginAs: String) {
        if (checkValidation(email, password)) {
            _loginState.value = UiState.Loading(true)
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    sendUserRole(loginAs)
                }.addOnFailureListener {
                    _loginState.value = UiState.Loading(false)
                    _loginState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
                }
        } else {
            val registerFieldsState = FieldsState(
                validateEmail(email), validatePassword(password, null)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun sendUserRole(loginAs: String) {
        viewModelScope.launch {
            val data = firestore.collection(USER_COLLECTION).document(auth.uid!!).get().await()
            val user = data.toObject<User>()

            if (user != null && !user.role.isNullOrEmpty()) {
                when (loginAs) {
                    "admin" -> {
                        _loginState.value = UiState.Loading(false)
                        if (user.role == loginAs) _loginState.value = UiState.Success(user.role)
                        else {
                            auth.signOut()
                            _loginState.value = UiState.Error("Akun tidak terdaftar sebagai admin")
                        }
                    }

                    "user" -> {
                        _loginState.value = UiState.Loading(false)
                        if (user.role == loginAs) _loginState.value = UiState.Success(user.role)
                        else {
                            auth.signOut()
                            _loginState.value = UiState.Error("Akun tidak terdaftar sebagai user")
                        }
                    }
                }
            } else {
                auth.signOut()
                _loginState.value = UiState.Error("User tidak ditemukan")
            }
        }
    }

    fun register(user: User, password: String, confirmPassword: String) {
        if (checkValidation(user.email!!, password, confirmPassword)) {
            _registerState.value = UiState.Loading(true)
            auth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                it.user?.let { firebaseUser ->
                    saveUserInfo(firebaseUser.uid, user)
                }
            }.addOnFailureListener {
                _loginState.value = UiState.Loading(false)
                _registerState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
            }
        } else {
            val registerFieldsState = FieldsState(
                validateEmail(user.email), validatePassword(password, confirmPassword)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(uid: String, user: User) {
        firestore.collection(USER_COLLECTION).document(uid).set(user).addOnSuccessListener {
            auth.signOut()
            _loginState.value = UiState.Loading(false)
            _registerState.value = UiState.Success("Berhasil daftar, silahkan masuk")
        }.addOnFailureListener {
            _loginState.value = UiState.Loading(false)
            _registerState.value = UiState.Error(it.message ?: "Terjadi kesalahan")
        }
    }

    private fun checkValidation(
        email: String, password: String, confirmPassword: String? = null
    ): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password, confirmPassword)

        return emailValidation is Validation.Success && passwordValidation is Validation.Success
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

}