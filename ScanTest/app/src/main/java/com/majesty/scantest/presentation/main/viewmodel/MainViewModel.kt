package com.majesty.scantest.presentation.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majesty.scantest.domain.RfidInterface
import com.majesty.scantest.util.RfidEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class MainViewModel @Inject constructor(
    private val rfidInterface: RfidInterface
) : ViewModel() {
    private val _rfidEvent = MutableLiveData<RfidEvent>()
    val rfidEvent: LiveData<RfidEvent> = _rfidEvent

    private val _epcData : MutableLiveData<String> = MutableLiveData()
    val epcData : LiveData<String> = _epcData

    init {
        enableRfidConnection()
    }

    fun doRfidSingleScan() = viewModelScope.launch {
        Log.d("TAG", "doRfidSingleScan: true")
        _rfidEvent.postValue(RfidEvent.OnScanning)
        rfidInterface.rfidSingleScan()
    }

    fun closeRfidScan() = viewModelScope.launch {
        _rfidEvent.postValue(RfidEvent.OnCloseScan)
        rfidInterface.rfidCloseScan()
    }

    fun closeRfidConnection() = viewModelScope.launch {
        _rfidEvent.postValue(RfidEvent.OnCloseScan)
        rfidInterface.rfidDisconnect()
    }

    fun enableRfidConnection() = viewModelScope.launch {
        rfidInterface.rfidConnect()
    }

    override fun onCleared() {
        viewModelScope.launch {
            rfidInterface.rfidDisconnect()
        }
        super.onCleared()
    }
}