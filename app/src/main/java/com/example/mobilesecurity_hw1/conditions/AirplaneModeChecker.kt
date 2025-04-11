package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ImageView

//Checker for airplane mode condition
class AirplaneModeChecker(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView) {

    private val handler = Handler(Looper.getMainLooper())
    private val checkRunnable = object : Runnable {
        override fun run() {
            checkAirplaneMode()
            handler.postDelayed(this, 2000)
        }
    }

    //Initialize the checker
    override fun initialize() {
        // No specific initialization needed
    }

    //Start checking for airplane mode
    override fun startChecking() {
        checkAirplaneMode() // Check immediately
        handler.post(checkRunnable) // Then check periodically
    }

    //Clean up resources
    override fun cleanup() {
        handler.removeCallbacks(checkRunnable)
    }

    //Check if airplane mode is on
    private fun checkAirplaneMode() {
        isConditionSatisfied = Settings.Global.getInt(
            activity.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
        updateUI()
    }
}