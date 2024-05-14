package com.majesty.scantest.data

import android.content.Context
import android.util.Log
import com.majesty.scantest.domain.RfidInterface
import com.pda.rfid.IAsynchronousMessage
import com.pda.rfid.uhf.UHFReader
import com.port.Adapt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HopeLandRepository(
    private val context: Context
) :RfidInterface {
    private val _dataRfidFlow = MutableSharedFlow<String>(replay = 0)
    private val dataRfidFlow : SharedFlow<String> = _dataRfidFlow

    private var scope: CoroutineScope? = null

    override suspend fun rfidConnect() {
        withContext(Dispatchers.Default){
            Adapt.init(context)
            Adapt.enablePauseInBackGround(context)
            scope?.cancel()
            scope = CoroutineScope(Dispatchers.IO)
            UHFReader.getUHFInstance().OpenConnect(rfid)
        }
    }

    override suspend fun rfidDisconnect() {
        scope?.cancel()
        withContext(Dispatchers.Default) {
            UHFReader._Config.CloseConnect()
        }
    }

    override suspend fun rfidScan() {
        TODO("Not yet implemented")
    }

    override suspend fun rfidSingleScan() = flow {
        UHFReader._Tag6C.GetEPC(1, 1)
        emit(dataRfidFlow.last())
    }

    override suspend fun rfidCloseScan() {
        UHFReader._Config.Stop()
    }

    override fun rfidDataObserved(): SharedFlow<String?> = dataRfidFlow

    private val rfid = IAsynchronousMessage {
        Log.d("TAG", "epc value: ${it._EPC}")
        scope?.launch {
            it._EPC?.let {
                epc -> _dataRfidFlow.emit(epc)
            }
        }
    }



}