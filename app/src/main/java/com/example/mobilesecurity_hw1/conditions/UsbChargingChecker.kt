package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

//Checker for USB charging condition
class UsbChargingChecker(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView) {

    private val handler = Handler(Looper.getMainLooper())
    private val checkRunnable = object : Runnable {
        override fun run() {
            checkChargingState()
            handler.postDelayed(this, 2000)
        }
    }

    //Initialize the checker
    override fun initialize() {
        // No specific initialization needed
    }

    //Start checking for USB charging
    override fun startChecking() {
        checkChargingState() // Check immediately
        handler.post(checkRunnable) // Then check periodically
    }

    //Clean up resources
    override fun cleanup() {
        handler.removeCallbacks(checkRunnable)
    }

    //Check if device is charging via USB (not wall)
    private fun checkChargingState() {
        val batteryStatus = activity.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        isConditionSatisfied = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        updateUI()
    }
}