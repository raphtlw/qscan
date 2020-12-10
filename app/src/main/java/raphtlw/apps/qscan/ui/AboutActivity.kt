package raphtlw.apps.qscan.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.databinding.ActivityAboutBinding
import raphtlw.apps.qscan.util.getHtmlString

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutText.text = resources.getHtmlString(R.string.about_text)
        binding.aboutText.movementMethod = LinkMovementMethod.getInstance()
    }
}