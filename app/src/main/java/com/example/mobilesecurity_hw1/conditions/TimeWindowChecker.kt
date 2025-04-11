package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.util.Calendar

//Checker for time window condition (first 20 seconds of a minute)
class TimeWindowChecker(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView) {

    private val handler = Handler(Looper.getMainLooper())
    private val checkRunnable = object : Runnable {
        override fun run() {
            checkTimeWindow()
            handler.postDelayed(this, 1000)
        }
    }

    //Initialize the checker
    override fun initialize() {
        // No specific initialization needed
    }

    //Start checking for the time window condition
    override fun startChecking() {
        handler.post(checkRunnable)
    }

    //Clean up resources
    override fun cleanup() {
        handler.removeCallbacks(checkRunnable)
    }

    //Check if current time is within the first 20 seconds of a minute
    private fun checkTimeWindow() {
        val calendar = Calendar.getInstance()
        val seconds = calendar.get(Calendar.SECOND)
        isConditionSatisfied = seconds < 20
        updateUI()
    }
}