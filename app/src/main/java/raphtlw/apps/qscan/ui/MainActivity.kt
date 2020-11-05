package raphtlw.apps.qscan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_scanner.*
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.ScanHistoryItem
import raphtlw.apps.qscan.util.saveScanHistoryItem
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val CAMERA_REQUEST_CODE = 101
    }

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var beepManager: BeepManager
    private lateinit var viewfinderView: ViewfinderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        viewfinderView = zxing_viewfinder_view
        viewfinderView.setLaserVisibility(true)
        barcodeView = barcode_scanner
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(
            arrayListOf(
                BarcodeFormat.QR_CODE
            )
        )
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(barcodeCallback)

        beepManager = BeepManager(this)
        beepManager.isBeepEnabled = false

        val scanHistoryButton: ExtendedFloatingActionButton = findViewById(R.id.history_fab)
        scanHistoryButton.setOnClickListener {
            val scanHistoryFragment = ScanHistoryFragment()
            scanHistoryFragment.show(supportFragmentManager, ScanHistoryFragment.TAG)
        }

        more_fab.setOnClickListener {
            startActivity(Intent(this, MoreActivity::class.java))
            overridePendingTransition(R.anim.bottom_up, 0)
        }

        val cameraPermission = checkSelfPermission(Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private val barcodeCallback = BarcodeCallback { result: BarcodeResult ->
        val currentTime = System.currentTimeMillis() / 1000L
        saveScanHistoryItem(
            applicationContext,
            ScanHistoryItem(
                URL(result.text).host, result.text, currentTime
            )
        )

        val openIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(result.text))
        if (openIntent.resolveActivity(packageManager) != null) {
            window.decorView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startActivity(openIntent)
        } else {
            Toast.makeText(this, "No apps are able to open the scanned item", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Camera permission granted")
                } else {
                    Log.i(TAG, "Camera permission denied")
                    Toast.makeText(
                        applicationContext,
                        "The camera permission is required to scan QR codes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}