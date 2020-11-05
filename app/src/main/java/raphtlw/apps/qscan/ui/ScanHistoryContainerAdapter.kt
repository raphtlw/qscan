package raphtlw.apps.qscan.ui

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ezvcard.Ezvcard
import ezvcard.io.CannotParseException
import raphtlw.apps.qscan.R
import raphtlw.apps.qscan.general.ScanHistoryItem
import java.text.SimpleDateFormat
import java.util.*

class ScanHistoryContainerAdapter(private val dataset: ArrayList<ScanHistoryItem>) :
    RecyclerView.Adapter<ScanHistoryContainerAdapter.ViewHolder>() {

    companion object {
        const val TAG = "ScanHistoryContainerAdapter"
    }

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
        holder.urlTextView.text = dataset[position].content
//            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
//                .withLocale(Locale.ENGLISH)
//                .withZone(ZoneId.systemDefault())
//                .format()
//                ?.toString()
        holder.timestampTextView.text =
            Date(dataset[position].timestamp * 1000L).let {
                val fmt = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                fmt.format(it)
            }

        holder.itemView.setOnClickListener { view ->
            val content = dataset[position].content
            val openIntent: Intent
            val vcard = try {
                Ezvcard.parse(content)
            } catch (e: CannotParseException) {
                null
            }

            if (URLUtil.isValidUrl(content)) {
                openIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content))
            } else if (vcard != null) {
                openIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    // Set email
                    putExtra(ContactsContract.Intents.Insert.EMAIL, vcard.first().emails[0].value)
                    // Set phone number
                    putExtra(ContactsContract.Intents.Insert.PHONE, vcard.first().telephoneNumbers[0].text)
                }
            } else {
                openIntent = Intent()
            }

            if (openIntent.resolveActivity(view.context.packageManager) != null) {
                view.context.startActivity(openIntent)
            } else {
                Toast.makeText(
                    view.context,
                    "No apps are able to open the scanned item",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i(TAG, "No apps can open the intent")
            }
        }
    }

    override fun getItemCount() = dataset.size
}