package com.amethamor.sleep.ui.report.monthly

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.amethamor.sleep.ui.report.saveBitmapToPictures
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import com.amethamor.sleep.ui.theme.SleepCheckAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.roundToInt

private const val MAX_EXPORT_HEIGHT_PX = 60000

suspend fun exportMonthlyReportAsPng(
    context: Context,
    data: MonthlyReportData,
    baseTheme: SleepBaseTheme
): Result<Uri> = runCatching {
    val bitmap = renderMonthlyReportBitmap(context, data, baseTheme)
    withContext(Dispatchers.IO) {
        saveBitmapToPictures(context, bitmap, data)
    }
}

private suspend fun renderMonthlyReportBitmap(
    context: Context,
    data: MonthlyReportData,
    baseTheme: SleepBaseTheme
): Bitmap = withContext(Dispatchers.Main) {
    val activity = context.findActivity() ?: error("Activity context is required")
    val root = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
    val exportWidthDp = MonthlyReportDesignTokens.ReportWidthDp
    val exportWidthPx = (exportWidthDp.value * activity.resources.displayMetrics.density).roundToInt()
    val composeView = ComposeView(activity)
    composeView.translationX = -exportWidthPx * 2f
    composeView.setViewTreeLifecycleOwner(root.findViewTreeLifecycleOwner())
    composeView.setViewTreeSavedStateRegistryOwner(root.findViewTreeSavedStateRegistryOwner())

    composeView.setContent {
        SleepCheckAppTheme(baseTheme = baseTheme) {
            MonthlyReportContent(
                data = data,
                exportMode = true,
                modifier = Modifier.width(exportWidthDp)
            )
        }
    }

    root.addView(
        composeView,
        ViewGroup.LayoutParams(exportWidthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
    )

    try {
        composeView.awaitTwoPosts()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(exportWidthPx, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(MAX_EXPORT_HEIGHT_PX, View.MeasureSpec.AT_MOST)
        composeView.measure(widthSpec, heightSpec)
        val height = composeView.measuredHeight.coerceAtLeast(1)
        composeView.layout(0, 0, exportWidthPx, height)
        Bitmap.createBitmap(exportWidthPx, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            composeView.draw(Canvas(bitmap))
        }
    } finally {
        root.removeView(composeView)
    }
}

private suspend fun View.awaitTwoPosts() {
    suspendCancellableCoroutine<Unit> { continuation ->
        post {
            post {
                if (continuation.isActive) continuation.resume(Unit)
            }
        }
    }
}

private fun saveBitmapToPictures(
    context: Context,
    bitmap: Bitmap,
    data: MonthlyReportData
): Uri {
    val fileName = "sleep_monthly_report_%04d_%02d.png".format(
        data.yearMonth.year,
        data.yearMonth.monthValue
    )
    return saveBitmapToPictures(context, bitmap, fileName)
}

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
