package raphtlw.apps.qscan.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.data.MoreItem
import raphtlw.apps.qscan.data.MoreItemType
import raphtlw.apps.qscan.databinding.ActivityMoreBinding
import kotlin.math.max

class MoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoreBinding
    private lateinit var moreContainer: RecyclerView
    private lateinit var moreContainerAdapter: RecyclerView.Adapter<*>
    private lateinit var moreContainerManager: RecyclerView.LayoutManager
    private lateinit var moreItems: Array<MoreItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.rootLayout.visibility = View.INVISIBLE

            val viewTreeObserver = binding.rootLayout.viewTreeObserver
            if (viewTreeObserver.isAlive) viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    circularRevealActivity()
                    binding.rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        moreItems = arrayOf(
            MoreItem("Generate QR Code", MoreItemType.TEXT) {
                startActivity(Intent(this, GenerateQrCodeActivity::class.java))
            },
            MoreItem("About", MoreItemType.TEXT) {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        )

        moreContainerManager = LinearLayoutManager(this)
        moreContainerAdapter = MoreContainerAdapter(moreItems)
        moreContainer = binding.moreContentContainer.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = moreContainerManager
            adapter = moreContainerAdapter
        }
    }

    private fun circularRevealActivity() {
        val cx = binding.rootLayout.width / 2
        val cy = binding.rootLayout.height / 2
        val finalRadius = max(binding.rootLayout.width, binding.rootLayout.height).toFloat()

        val circularReveal = ViewAnimationUtils.createCircularReveal(
            binding.rootLayout, cx, cy,
            0F, finalRadius
        )
        circularReveal.duration = 1000

        binding.rootLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.bottom_down)
    }
}