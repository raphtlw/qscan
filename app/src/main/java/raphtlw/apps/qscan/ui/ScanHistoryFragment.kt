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

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
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
            root.findViewById<ViewFlipper>(R.id.scan_history_list_flipper).showNext()
        } else {
            viewManager = LinearLayoutManager(context)
            viewAdapter = ViewAdapter(scanHistoryData)
            recyclerView = root.findViewById<RecyclerView>(R.id.scan_history_list).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

        return root
    }
}

class ViewAdapter(private val dataset: ArrayList<ScanHistoryItem>) :
    RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.scan_history_item_name)
        val urlTextView: TextView = view.findViewById(R.id.scan_history_item_url)
        val timestampTextView: TextView = view.findViewById(R.id.scan_history_item_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.scan_history_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = dataset[position].name
        holder.urlTextView.text = dataset[position].link
//            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
//                .withLocale(Locale.ENGLISH)
//                .withZone(ZoneId.systemDefault())
//                .format()
//                ?.toString()
        holder.timestampTextView.text =
            Date(dataset[position].timestamp * 1000L).let {
                val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                fmt.format(it)
            }

        holder.itemView.setOnClickListener { view ->
            val openIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(dataset[position].link))
            if (openIntent.resolveActivity(view.context.packageManager) != null) {
                view.context.startActivity(openIntent)
            } else {
                Log.i(view.context.packageName, "No apps can open the intent")
            }
        }
    }

    override fun getItemCount() = dataset.size
}