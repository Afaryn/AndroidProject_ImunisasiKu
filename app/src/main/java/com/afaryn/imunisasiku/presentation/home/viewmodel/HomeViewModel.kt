package com.afaryn.imunisasiku.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.imunisasiku.model.Imunisasi
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants.IMUNISASI_COLLECTION
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.afaryn.imunisasiku.utils.getClosestDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _homeState = MutableStateFlow<UiState<Imunisasi>>(UiState.Loading(false))
    val homeState = _homeState.asStateFlow().asLiveData()
    private val _userState = MutableStateFlow<UiState<String>>(UiState.Loading(false))
    val userState = _userState.asStateFlow().asLiveData()

    init {
        getClosestImunisasi()
        getUserName()
    }

    private fun getUserName() {
        firestore.collection(USER_COLLECTION).document(auth.uid!!).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("HomeViewModel", error.message ?: "Terjadi Kesalahan")
                return@addSnapshotListener
            }

            value?.let {
                val user = value.toObject(User::class.java)
                user?.let {
                    _userState.value = UiState.Success(it.name ?: "-")
                }
            }
        }
    }

    private fun getClosestImunisasi() {
        _homeState.value = UiState.Loading(true)
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(IMUNISASI_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _homeState.value = UiState.Loading(false)
                    _homeState.value = UiState.Error(error.localizedMessage ?: "Terjadi Kesalahan")
                    return@addSnapshotListener
                }

                value?.let {
                    _homeState.value = UiState.Loading(false)
                    val imunisasi = value.toObjects(Imunisasi::class.java)

                    // Get the value of the closest date
                    val dates = mutableListOf<String>()
                    imunisasi.forEach {
                        it.jadwalImunisasi?.let { jadwal -> dates.add(jadwal) }
                    }

                    // Filter to get data matching the closest date
                    val closestDate = getClosestDate(dates, imunisasi)
                    if (closestDate != null) {
                        _homeState.value = UiState.Success(closestDate)
                    }else {
                        _homeState.value = UiState.Loading(false)
                    }
                }
            }
    }
}