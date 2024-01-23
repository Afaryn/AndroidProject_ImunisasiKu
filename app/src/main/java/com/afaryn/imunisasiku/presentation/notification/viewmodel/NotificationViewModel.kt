package com.afaryn.imunisasiku.presentation.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.imunisasiku.notification.database.NotificationDao
import com.afaryn.imunisasiku.notification.database.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationDao: NotificationDao
): ViewModel() {
    private val _notificationState = MutableStateFlow<List<NotificationEntity>>(listOf())
    val notificationState = _notificationState.asStateFlow()

    init {
        getAllNotifications()
    }

    private fun getAllNotifications() {
        viewModelScope.launch {
            notificationDao.getAllNotifications().collect {
                _notificationState.value = it
            }
        }
    }

    fun deleteAllNotification() {
        viewModelScope.launch {
            notificationDao.deleteAllNotification()
        }
    }
}