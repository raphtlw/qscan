package raphtlw.apps.qscan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_scan_history.*
import kotlinx.android.synthetic.main.fragment_scan_history.view.*
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.ScanHistoryItem
import raphtlw.apps.qscan.util.getScanHistoryItems
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ScanHistoryFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ScanHistoryFragment"
    }

    private lateinit var scanHistoryContainer: RecyclerView
    private lateinit var scanHistoryContainerAdapter: RecyclerView.Adapter<*>
    private lateinit var scanHistoryContainerManager: RecyclerView.LayoutManager
    private lateinit var scanHistoryData: ArrayList<ScanHistoryItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_scan_history, container, false)

        try {
            scanHistoryData = getScanHistoryItems(requireContext())
        } catch (e: JsonSyntaxException) {
            Log.i(TAG, e.toString())
            Toast.makeText(
                requireContext(),
                "Please clear the app storage or redownload the app!",
                Toast.LENGTH_LONG
            ).show()
            requireActivity().finishAffinity()
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                this.data = Uri.fromParts("package", requireContext().packageName, null)
                startActivity(this)
            }
            exitProcess(0)
        }

        if (scanHistoryData.isEmpty()) {
            root.scan_history_list_flipper.showNext()
        } else {
            scanHistoryContainerManager = LinearLayoutManager(context)
            scanHistoryContainerAdapter = ScanHistoryContainerAdapter(scanHistoryData)
            scanHistoryContainer = root.scan_history_list.apply {
                setHasFixedSize(true)
                layoutManager = scanHistoryContainerManager
                adapter = scanHistoryContainerAdapter
            }
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }
}