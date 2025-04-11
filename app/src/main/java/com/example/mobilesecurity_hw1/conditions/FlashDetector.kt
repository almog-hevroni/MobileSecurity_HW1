package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.ImageView

//Checker for flash detection condition
class FlashDetector(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    // Light threshold for flash detection
    private val flashLightThreshold = 200f
    private var baselineLightLevel = 0f
    private var isLightBaselineSet = false

    //Initialize the light sensor
    override fun initialize() {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    //Start checking for flash detection
    override fun startChecking() {
        isLightBaselineSet = false // Reset baseline
        lightSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    //Clean up resources
    override fun cleanup() {
        sensorManager.unregisterListener(this)
    }

    //Reset the condition
    override fun reset() {
        super.reset()
        isLightBaselineSet = false
    }

    //Handle light sensor events
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0]
            handleLightSensor(lightValue)
        }
    }

    //Process light sensor data and detect flash
    private fun handleLightSensor(lightValue: Float) {
        if (!isLightBaselineSet) {
            baselineLightLevel = lightValue
            isLightBaselineSet = true
        }

        // Detect sudden large increase in light (flash)
        if (lightValue > baselineLightLevel + flashLightThreshold) {
            isConditionSatisfied = true
            updateUI()
        }
    }

    //Handle sensor accuracy changes
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}