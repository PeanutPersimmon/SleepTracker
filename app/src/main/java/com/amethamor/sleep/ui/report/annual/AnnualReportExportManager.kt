package com.amethamor.sleep.ui.report.annual

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.report.saveBitmapToPictures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AnnualReportExportManager {
    suspend fun exportAnnualReportImage(
        context: Context,
        records: List<SleepRecord>,
        year: Int
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val report = AnnualReportCalculator.buildAnnualSleepReport(records, year)
            val bitmap = AnnualReportImageRenderer.renderAnnualReportBitmap(context, report)
            val fileName = "Sleep_Annual_Report_${year}.png"
            saveBitmapToPictures(context, bitmap, fileName)
        }
    }
}
