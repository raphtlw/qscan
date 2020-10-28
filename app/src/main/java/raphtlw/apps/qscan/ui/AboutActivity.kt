package raphtlw.apps.qscan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.activity_about.*
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.util.getHtmlString

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_text.text = resources.getHtmlString(R.string.about_text)
        about_text.movementMethod = LinkMovementMethod.getInstance()
    }
}