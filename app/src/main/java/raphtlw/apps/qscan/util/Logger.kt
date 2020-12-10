package raphtlw.apps.qscan.util

import android.util.Log

class Logger(obj: Any) {
    private lateinit var tag: String

    init {
        when (obj) {
            is Class<*> -> {
                this.tag = obj.simpleName
            }
            is String -> {
                this.tag = obj
            }
        }
    }

    fun v(msg: String) {
        Log.v(tag, msg)
    }

    fun d(msg: String) {
        Log.d(tag, msg)
    }

    fun i(msg: String) {
        Log.i(tag, msg)
    }

    fun w(msg: String) {
        Log.i(tag, msg)
    }

    fun e(msg: String) {
        Log.e(tag, msg)
    }

    fun e(tr: Throwable) {
        Log.e(tag, tr.stackTraceToString())
    }

    fun wtf(msg: String) {
        Log.wtf(tag, msg)
    }
}