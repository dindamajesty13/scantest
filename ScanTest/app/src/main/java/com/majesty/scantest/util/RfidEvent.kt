package com.majesty.scantest.util

sealed class RfidEvent {
    object OnScanning : RfidEvent()
    object OnCloseScan : RfidEvent()
}