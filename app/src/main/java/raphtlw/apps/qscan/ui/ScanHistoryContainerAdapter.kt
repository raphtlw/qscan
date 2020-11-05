package raphtlw.apps.qscan.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.ScanHistoryItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScanHistoryContainerAdapter(private val dataset: ArrayList<ScanHistoryItem>) :
    RecyclerView.Adapter<ScanHistoryContainerAdapter.ViewHolder>() {

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