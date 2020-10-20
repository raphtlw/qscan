package raphtlw.apps.qscan

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.custom_scanner.*
import raphtlw.apps.qscan.R

class ScannerActivity : AppCompatActivity() {
    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var viewfinderView: ViewfinderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        viewfinderView = zxing_viewfinder_view
        barcodeScannerView = zxing_barcode_scanner
        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()
        viewfinderView.setLaserVisibility(true)
        zxing_barcode_surface.width

        val scanHistoryButton: Button = findViewById(R.id.history_fab)
        scanHistoryButton.setOnClickListener {
            val scanHistoryFragment = ScanHistoryFragment()
            scanHistoryFragment.show(supportFragmentManager, ScanHistoryFragment.TAG)
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}