package raphtlw.apps.qscan

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startScanner()
    }

    private fun startScanner() {
        IntentIntegrator(this).setOrientationLocked(false)
            .setCaptureActivity(ScannerActivity::class.java)
            .setBeepEnabled(false)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .initiateScan()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            startScanner()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val toastMessage: String = if (scanResult.contents == null) {
                "Cancelled from fragment"
            } else {
                "Scanned from fragment: " + scanResult.contents
            }
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }

        val openIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(scanResult.contents))
        if (openIntent.resolveActivity(packageManager) != null) {
            window.decorView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startActivity(openIntent)
        } else {
            Toast.makeText(this, "No apps can open the intent", Toast.LENGTH_SHORT).show()
        }
    }
}