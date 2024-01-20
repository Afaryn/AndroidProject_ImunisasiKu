package com.afaryn.imunisasiku.admin.ui.home.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.imunisasiku.model.JenisImunisasi
import com.afaryn.imunisasiku.model.Pasien
import com.afaryn.imunisasiku.model.User
import com.afaryn.imunisasiku.utils.Constants.JENIS_IMUNISASI
import com.afaryn.imunisasiku.utils.Constants.PASIEN_COLLECTION
import com.afaryn.imunisasiku.utils.Constants.USER_COLLECTION
import com.afaryn.imunisasiku.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeAdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    ):ViewModel() {



    private val _getState = MutableStateFlow<UiState<Map<String,String>>>(UiState.Loading(false))
    val getState = _getState.asStateFlow().asLiveData()



    suspend fun homePageData():Map<String,String>{
        val map = mutableMapOf<String,String>()
        _getState.value=UiState.Loading(true)
        firestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener {
                val akun = it.toObjects(User::class.java)
                map["akun"]=akun.size.toString()
            }
            .await()
        firestore.collection(JENIS_IMUNISASI).get()
            .addOnSuccessListener {
                val imn = it.toObjects(JenisImunisasi::class.java)
                map["imunisasi"]=imn.size.toString()
            }
            .await()
        firestore.collection(PASIEN_COLLECTION).get()
            .addOnSuccessListener {
                val psn = it.toObjects(Pasien::class.java)
                map["pasien"]=psn.size.toString()
            }
            .await()
        firestore.collection(USER_COLLECTION).document(auth.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val akun = it.toObject(User::class.java)
                    map["username"] = akun!!.name.toString()

                }
            }
            .await()

        return map
    }

    fun getData(){
        viewModelScope.launch {
            try {
                val data = homePageData()
                _getState.value = UiState.Success(data)
                _getState.value = UiState.Loading(false)
            }catch (e: Exception) {
                // Menangani exception
                Log.e("HomeAdminViewModel", "Error fetching home page data", e)
                _getState.value = UiState.Loading(false)
                _getState.value = UiState.Error("Error fetching home page data")
            }
        }
    }
}