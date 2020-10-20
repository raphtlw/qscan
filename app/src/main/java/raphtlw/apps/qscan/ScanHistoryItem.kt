package raphtlw.apps.qscan

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

fun scanHistoryFromJson(json: String): ScanHistoryItem = Gson().fromJson(json, ScanHistoryItem::class.java)

fun scanHistoryArrayFromJson(json: String): ArrayList<ScanHistoryItem> =
    Gson().fromJson(
        json,
        object : TypeToken<ArrayList<ScanHistoryItem>>() {}.type
    )

data class ScanHistoryItem(val name: String, val link: String, val timestamp: String) {
    fun toJson(): String = Gson().toJson(this)
    fun toJsonArray(): String = Gson().toJson(arrayListOf(this))
}