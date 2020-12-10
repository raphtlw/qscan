package raphtlw.apps.qscan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.*
import com.squareup.moshi.JsonDataException
import ezvcard.Ezvcard
import ezvcard.io.CannotParseException
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.data.ScanHistory
import raphtlw.apps.qscan.databinding.ActivityMainBinding
import raphtlw.apps.qscan.util.Logger
import java.net.URL
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_REQUEST_CODE = 101
    }

    private val log: Logger = Logger(this.javaClass)
    private lateinit var binding: ActivityMainBinding
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var beepManager: BeepManager
    private lateinit var viewfinderView: ViewfinderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        viewfinderView = binding.barcodeScanner.viewFinder
        viewfinderView.setLaserVisibility(true)
        barcodeView = binding.barcodeScanner
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(
            arrayListOf(
                BarcodeFormat.QR_CODE
            )
        )
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(barcodeCallback)

        beepManager = BeepManager(this)
        beepManager.isBeepEnabled = false

        findViewById<ExtendedFloatingActionButton>(R.id.history_fab).setOnClickListener {
            val scanHistoryFragment = ScanHistoryFragment()
            scanHistoryFragment.show(supportFragmentManager, "ScanHistoryFragment")
        }

        findViewById<FloatingActionButton>(R.id.more_fab).setOnClickListener {
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
        log.i("Scan result: ${result.text}")
        val currentTime = System.currentTimeMillis() / 1000L

        val openIntent: Intent
        val vcard = try {
            Ezvcard.parse(result.text)
        } catch (e: CannotParseException) {
            null
        }

        when {
            URLUtil.isValidUrl(result.text) -> {
                try {
                    ScanHistory.saveScanHistoryItem(
                        applicationContext,
                        ScanHistory(
                            result.text, URL(result.text).host, currentTime
                        )
                    )
                } catch (e: JsonDataException) {
                    log.i(e.toString())
                    Toast.makeText(
                        applicationContext,
                        "Please clear the app storage or redownload the app!",
                        Toast.LENGTH_LONG
                    ).show()
                    this.finishAffinity()
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        this.data = Uri.fromParts("package", applicationContext.packageName, null)
                        startActivity(this)
                    }
                    exitProcess(0)
                }
                openIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(result.text))
            }
            vcard != null -> {
                ScanHistory.saveScanHistoryItem(
                    applicationContext,
                    ScanHistory(
                        vcard.first().formattedName.value, "Contact", currentTime
                    )
                )
                openIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    // Set email
                    putExtra(ContactsContract.Intents.Insert.EMAIL, vcard.first().emails[0].value)
                    // Set phone number
                    putExtra(
                        ContactsContract.Intents.Insert.PHONE,
                        vcard.first().telephoneNumbers[0].text
                    )
                }
            }
            else -> {
                openIntent = Intent()
            }
        }

        if (openIntent.resolveActivity(packageManager) != null) {
            window.decorView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startActivity(openIntent)
        } else {
            Toast.makeText(
                applicationContext,
                "No apps are able to open the scanned item",
                Toast.LENGTH_SHORT
            ).show()
            log.i("No apps can open the intent")
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
                    log.i("Camera permission granted")
                } else {
                    log.i("Camera permission denied")
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