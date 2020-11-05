package raphtlw.apps.qscan.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_more.*
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.MoreItem
import raphtlw.apps.qscan.general.MoreItemType
import kotlin.math.max

class MoreActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MoreActivity"
    }

    private lateinit var moreContainer: RecyclerView
    private lateinit var moreContainerAdapter: RecyclerView.Adapter<*>
    private lateinit var moreContainerManager: RecyclerView.LayoutManager
    private lateinit var moreItems: Array<MoreItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        if (savedInstanceState == null) {
            root_layout.visibility = View.INVISIBLE

            val viewTreeObserver = root_layout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity()
                        root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
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
        moreContainer = more_content_container.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = moreContainerManager
            adapter = moreContainerAdapter
        }
    }

    private fun circularRevealActivity() {
        val cx = root_layout.width / 2
        val cy = root_layout.height / 2
        val finalRadius = max(root_layout.width, root_layout.height).toFloat()

        val circularReveal = ViewAnimationUtils.createCircularReveal(root_layout, cx, cy,
            0F, finalRadius)
        circularReveal.duration = 1000

        root_layout.visibility = View.VISIBLE
        circularReveal.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.bottom_down)
    }
}