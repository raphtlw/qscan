package raphtlw.apps.qscan.general

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScanHistory(val name: String, val content: String, val timestamp: Long) {
    fun toJson(): String = Gson().toJson(this)
    fun toJsonArray(): String = Gson().toJson(arrayListOf(this))

    companion object {
        fun fromJson(json: String): ScanHistory =
            Gson().fromJson(json, ScanHistory::class.java)

        fun fromJsonArray(json: String): ArrayList<ScanHistory> =
            Gson().fromJson(
                json,
                object : TypeToken<ArrayList<ScanHistory>>() {}.type
            )
    }
}