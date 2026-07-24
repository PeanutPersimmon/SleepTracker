package com.amethamor.sleep.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

class RestartActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "RestartActivity onCreate in process=${android.os.Process.myPid()}")

        val mainPid = intent.getIntExtra(AppRestartUtils.EXTRA_MAIN_PID, -1)

        Handler(Looper.getMainLooper()).postDelayed({
            if (mainPid > 0 && mainPid != android.os.Process.myPid()) {
                Log.d(TAG, "RestartActivity killing main process pid=$mainPid")
                android.os.Process.killProcess(mainPid)
            } else {
                Log.d(TAG, "RestartActivity mainPid invalid: $mainPid")
            }

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                try {
                    Log.d(TAG, "RestartActivity launching main activity")

                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent == null) {
                        Log.e(TAG, "launchIntent is null")
                        finish()
                        return@Runnable
                    }

                    val componentName = launchIntent.component
                    val restartIntent = if (componentName != null) {
                        Intent.makeRestartActivityTask(componentName)
                    } else {
                        launchIntent.apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    }

                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(restartIntent)
                    Log.d(TAG, "RestartActivity launch main returned")

                    finish()

                    Handler(Looper.getMainLooper()).postDelayed({
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }, EXIT_DELAY_MILLIS)
                } catch (e: Exception) {
                    Log.e(TAG, "RestartActivity failed to launch main", e)
                    finish()
                }
            }, LAUNCH_DELAY_MILLIS)
        }, KILL_DELAY_MILLIS)
    }

    companion object {
        private const val TAG = "SleepRestart"
        private const val KILL_DELAY_MILLIS = 300L
        private const val LAUNCH_DELAY_MILLIS = 300L
        private const val EXIT_DELAY_MILLIS = 300L
    }
}
