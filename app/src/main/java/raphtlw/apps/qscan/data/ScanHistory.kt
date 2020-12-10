package raphtlw.apps.qscan.data

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import raphtlw.apps.qscan.util.Logger
import java.io.*

@JsonClass(generateAdapter = true)
data class ScanHistory(val content: String, val name: String, val timestamp: Long) {

    fun toJson(): String {
        val moshi = Moshi.Builder().build()
        val adapter = ScanHistoryJsonAdapter(moshi)
        return adapter.toJson(this)
    }

    companion object {
        fun toJsonArray(item: MutableList<ScanHistory>): String {
            val scanHistoryListType =
                Types.newParameterizedType(MutableList::class.java, ScanHistory::class.java)
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<MutableList<ScanHistory>> = moshi.adapter(scanHistoryListType)
            return adapter.toJson(item)
        }

        fun fromJson(string: String): ScanHistory {
            val moshi = Moshi.Builder().build()
            val adapter = ScanHistoryJsonAdapter(moshi)
            return adapter.fromJson(string)!!
        }

        fun fromJsonArray(string: String): MutableList<ScanHistory> {
            val scanHistoryListType =
                Types.newParameterizedType(MutableList::class.java, ScanHistory::class.java)
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<MutableList<ScanHistory>> = moshi.adapter(scanHistoryListType)
            return adapter.fromJson(string)!!
        }

        fun saveScanHistoryItem(context: Context, item: ScanHistory) {
            val log = Logger(context.packageName)

            val filePath = "${context.filesDir.absolutePath}/scan_history.json"
            log.i(filePath)
            val file = File(filePath)

            if (file.exists()) {
                log.i("File already exists")
                val fileJsonString = BufferedReader(FileReader(file)).readLines().joinToString("\n")
                val fileJson = fromJsonArray(fileJsonString)

                fileJson.add(item)
                val newFileJsonString = toJsonArray(fileJson)

                BufferedWriter(FileWriter(file)).use { out ->
                    out.write(newFileJsonString)
                }
            } else {
                log.d(item.toJson())
                val itemJsonString = toJsonArray(arrayListOf(item))
                file.createNewFile()
                log.i("File created")
                log.i("Item JSON: $itemJsonString")
                BufferedWriter(FileWriter(file)).use { out ->
                    out.write(itemJsonString)
                }
            }

            log.i("Scan history item saved to file")
        }

        fun getScanHistoryItems(context: Context): ArrayList<ScanHistory> {
            val log = Logger(context.packageName)

            val filePath = "${context.filesDir.absolutePath}/scan_history.json"
            log.i(filePath)
            val file = File(filePath)

            return if (file.exists()) {
                val fileJsonString = BufferedReader(FileReader(file)).readLines().joinToString("\n")
                fromJsonArray(fileJsonString) as ArrayList<ScanHistory>
            } else {
                arrayListOf()
            }
        }
    }
}