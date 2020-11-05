package raphtlw.apps.qscan.util

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import raphtlw.apps.qscan.general.ScanHistoryItem
import raphtlw.apps.qscan.general.scanHistoryArrayFromJson
import java.io.*

fun saveScanHistoryItem(context: Context, item: ScanHistoryItem) {
    val filePath = "${context.filesDir.absolutePath}/scan_history.json"
    Log.i(context.packageName, filePath)
    val file = File(filePath)

    if (file.exists()) {
        Log.i(context.packageName, "File already exists")
        val fileJsonString = BufferedReader(FileReader(file)).readLines().joinToString("\n")
        val fileJson = scanHistoryArrayFromJson(fileJsonString)

        fileJson.add(item)
        val newFileJsonString = Gson().toJson(fileJson)

        BufferedWriter(FileWriter(file)).use { out ->
            out.write(newFileJsonString)
        }
    } else {
        Log.d(context.packageName, item.toJson())
        val itemJsonString = item.toJsonArray()
        file.createNewFile()
        Log.i(context.packageName, "File created")
        Log.i(context.packageName, "Item JSON: $itemJsonString")
        BufferedWriter(FileWriter(file)).use { out ->
            out.write(itemJsonString)
        }
    }

    Log.i(context.packageName, "Scan history item saved to file")
}

fun getScanHistoryItems(context: Context): ArrayList<ScanHistoryItem> {
    val filePath = "${context.filesDir.absolutePath}/scan_history.json"
    Log.i(context.packageName, filePath)
    val file = File(filePath)

    return if (file.exists()) {
        val fileJsonString = BufferedReader(FileReader(file)).readLines().joinToString("\n")
        scanHistoryArrayFromJson(fileJsonString)
    } else {
        arrayListOf()
    }
}

fun Resources.getHtmlString(@StringRes id: Int) =
    HtmlCompat.fromHtml(getString(id), HtmlCompat.FROM_HTML_MODE_COMPACT)