package com.amethamor.sleep.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

object AppRestartUtils {
    private const val TAG = "SleepRestart"
    const val EXTRA_MAIN_PID = "extra_main_pid"

    fun restartApp(context: Context) {
        Log.d(TAG, "AppRestartUtils.restartApp entered")

        try {
            val appContext = context.applicationContext
            val mainPid = android.os.Process.myPid()

            val intent = Intent(appContext, RestartActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(EXTRA_MAIN_PID, mainPid)
            }

            Log.d(TAG, "Starting RestartActivity, mainPid=$mainPid")
            appContext.startActivity(intent)
            Log.d(TAG, "RestartActivity startActivity returned")

            if (context is Activity) {
                context.finishAffinity()
            }
        } catch (e: Exception) {
            Log.e(TAG, "restartApp failed", e)
        }
    }
}
