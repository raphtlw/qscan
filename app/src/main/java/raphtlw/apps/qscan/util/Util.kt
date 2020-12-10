package raphtlw.apps.qscan.util

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat

fun Resources.getHtmlString(@StringRes id: Int) =
    HtmlCompat.fromHtml(getString(id), HtmlCompat.FROM_HTML_MODE_COMPACT)