package raphtlw.apps.qscan.general

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun scanHistoryFromJson(json: String): ScanHistoryItem = Gson().fromJson(json, ScanHistoryItem::class.java)

fun scanHistoryArrayFromJson(json: String): ArrayList<ScanHistoryItem> =
    Gson().fromJson(
        json,
        object : TypeToken<ArrayList<ScanHistoryItem>>() {}.type
    )

data class ScanHistoryItem(val name: String, val link: String, val timestamp: Long) {
    fun toJson(): String = Gson().toJson(this)
    fun toJsonArray(): String = Gson().toJson(arrayListOf(this))
}