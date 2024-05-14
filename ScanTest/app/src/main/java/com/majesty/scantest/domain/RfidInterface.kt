package com.majesty.scantest.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface RfidInterface {
    suspend fun rfidConnect()

    suspend fun rfidDisconnect()

    suspend fun rfidScan()

    suspend fun rfidSingleScan() : Flow<String>

    suspend fun rfidCloseScan()

    fun rfidDataObserved() : SharedFlow<String?>
}