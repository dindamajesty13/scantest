package com.majesty.scantest.presentation.main.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.majesty.scantest.R
import com.majesty.scantest.presentation.main.viewmodel.MainViewModel
import com.majesty.scantest.ui.theme.ScanTestTheme
import com.majesty.scantest.util.Constants
import com.majesty.scantest.util.RfidEvent
import com.majesty.scantest.util.SoundUtils
import com.majesty.scantest.util.createAlertDialog
import com.majesty.scantest.util.createAppSettingIntent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private var isPressed: Boolean = false
    private var epc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObserver()

        initPermission()

        setContent {
            ScanTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(viewModel = viewModel, isScanning(), epc)
                }
            }
        }
    }

    private fun initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_PHONE_STATE_PERMISSION)
        }
    }

    private fun isScanning(): Boolean = with(viewModel){
        rfidEvent.value == RfidEvent.OnScanning
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (Constants.isValidKey(keyCode)) {
            viewModel.closeRfidScan()
            isPressed = false
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (Constants.isValidKey(keyCode)){
            viewModel.doRfidSingleScan()
            if (!isPressed){
                isPressed = true
                if (isScanning()) {
                    viewModel.closeRfidScan()
                }
                viewModel.doRfidSingleScan()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setupObserver() = with(viewModel) {
        rfidEvent.observe(this@MainActivity) {

        }

        epcData.observe(this@MainActivity) {
            epc = epcData.toString()
            Log.d("TAG", "setupObserver: $epc")
            SoundUtils.startSound()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PHONE_STATE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    createAlertDialog(
                        title = "Open Settings",
                        caption = "Please give permission",
                        positiveButtonText = "Open Settings",
                        positiveListener = {
                            startActivity(createAppSettingIntent())
                        },
                        negativeButtonText = "Cancel",
                        negativeListener = {

                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.enableRfidConnection()
    }

    override fun onPause() {
        super.onPause()
        viewModel.closeRfidConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundUtils.releaseSound()
    }

    companion object {
        const val REQUEST_PHONE_STATE_PERMISSION = 123
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: MainViewModel, isScanning: Boolean, epc: String) {
    Scaffold(
        topBar = {TopAppBar(
            title = {Text("Scanner App")},
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
            ),
        ) },
        content = { MyContent(viewModel = viewModel, isScanning, epc) }
    )
}

@Composable
fun MyContent(viewModel: MainViewModel, isScanning: Boolean, epc: String) {
    Column(
        // on the below line we are specifying modifier
        // and setting max height and max width
        // for our column
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            // on below line we are
            // adding padding for our column
            .padding(5.dp),
        // on below line we are specifying horizontal
        // and vertical alignment for our column
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "EPC Result Here!",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))

        // on below line adding a button to open URL
        Button(onClick = {
            runScanner(viewModel, false)
            Log.d("TAG", "MyContent: press")
        }) {
            Column(
                // on below line we are specifying modifier
                // and setting max height and max width
                // for our column
                modifier = Modifier
                    // on below line we are
                    // adding padding for our column
                    .padding(5.dp),
                // on the below line we are specifying horizontal
                // and vertical alignment for our column
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    // on below line we are specifying the drawable
                    // image for our image.
                    painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),

                    // on below line we are specifying
                    // content description for our image
                    contentDescription = "Image",

                    // on below line we are setting height
                    // and width for our image.
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)

                )
                // adding spacer on below line.
                Spacer(Modifier.height(10.dp))

                // adding text on below line.
                Text(
                    // specifying text as android
                    "Scan",

                    // on below line adding style
                    style = TextStyle(fontWeight = FontWeight.Bold),

                    // adding text align on below line.
                    textAlign = TextAlign.Center,

                    // adding font size on below line.
                    fontSize = 20.sp
                )
            }
        }
    }
}

fun runScanner(viewModel: MainViewModel, isScanning: Boolean) {
    if (isScanning){
        viewModel.closeRfidScan()
    }else {
        viewModel.doRfidSingleScan()
    }
}
