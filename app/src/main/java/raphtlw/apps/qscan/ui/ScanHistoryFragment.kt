package raphtlw.apps.qscan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.moshi.JsonDataException
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.data.ScanHistory
import raphtlw.apps.qscan.databinding.FragmentScanHistoryBinding
import raphtlw.apps.qscan.util.Logger
import kotlin.system.exitProcess

class ScanHistoryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentScanHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var scanHistoryContainer: RecyclerView
    private lateinit var scanHistoryContainerAdapter: RecyclerView.Adapter<*>
    private lateinit var scanHistoryContainerManager: RecyclerView.LayoutManager
    private lateinit var scanHistoryData: ArrayList<ScanHistory>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanHistoryBinding.inflate(inflater, container, false)
        val view = binding.root
        val log = Logger(this.javaClass)

        try {
            scanHistoryData = ScanHistory.getScanHistoryItems(requireContext())
        } catch (e: JsonDataException) {
            log.i(e.toString())
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
            binding.scanHistoryListFlipper.showNext()
        } else {
            scanHistoryContainerManager = LinearLayoutManager(context).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            scanHistoryContainerAdapter = ScanHistoryContainerAdapter(scanHistoryData)
            scanHistoryContainer = binding.scanHistoryList.apply {
                setHasFixedSize(true)
                layoutManager = scanHistoryContainerManager
                adapter = scanHistoryContainerAdapter
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }
}