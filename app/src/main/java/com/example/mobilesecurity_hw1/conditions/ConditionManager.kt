package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.widget.FrameLayout
import android.widget.ImageView

//Manager for all condition checkers
class ConditionManager(private val activity: Activity) {

    // List of all condition checkers
    private val conditionCheckers = mutableListOf<ConditionChecker>()

    // Individual condition checkers
    private lateinit var timeWindowChecker: TimeWindowChecker
    private lateinit var touchPatternChecker: TouchPatternChecker
    private lateinit var usbChargingChecker: UsbChargingChecker
    private lateinit var flashDetector: FlashDetector
    private lateinit var airplaneModeChecker: AirplaneModeChecker
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var hatDetector: HatDetector

    //Initialize all condition checkers
    fun initialize(
        smiley1: ImageView, // Time window
        smiley2: ImageView, // Touch pattern
        smiley3: ImageView, // USB charging
        smiley4: ImageView, // Flash detection
        smiley5: ImageView, // Airplane mode
        smiley6: ImageView, // Barcode scanning
        smiley7: ImageView, // Hat detection
        touchAreaTopLeft: FrameLayout,
        touchAreaCenter: FrameLayout,
        touchAreaBottomLeft: FrameLayout
    ) {
        // Create condition checkers
        timeWindowChecker = TimeWindowChecker(activity, smiley1)
        touchPatternChecker = TouchPatternChecker(
            activity, smiley2, touchAreaTopLeft, touchAreaCenter, touchAreaBottomLeft
        )
        usbChargingChecker = UsbChargingChecker(activity, smiley3)
        flashDetector = FlashDetector(activity, smiley4)
        airplaneModeChecker = AirplaneModeChecker(activity, smiley5)
        barcodeScanner = BarcodeScanner(activity, smiley6)
        hatDetector = HatDetector(activity, smiley7)

        // Add to the list
        conditionCheckers.add(timeWindowChecker)
        conditionCheckers.add(touchPatternChecker)
        conditionCheckers.add(usbChargingChecker)
        conditionCheckers.add(flashDetector)
        conditionCheckers.add(airplaneModeChecker)
        conditionCheckers.add(barcodeScanner)
        conditionCheckers.add(hatDetector)

        // Initialize each checker
        conditionCheckers.forEach { it.initialize() }
    }

    //Start all condition checks
    fun startChecking() {
        conditionCheckers.forEach { it.startChecking() }
    }

    //Check if all conditions are met
    fun areAllConditionsMet(): Boolean {
        return conditionCheckers.all { it.isConditionMet() }
    }

    //Clean up resources
    fun cleanup() {
        conditionCheckers.forEach { it.cleanup() }
    }

    //Resume checking
    fun resume() {
        conditionCheckers.forEach {
            if (!it.isConditionMet()) {
                it.reset()
                it.startChecking()
            }
        }
    }

    // Access specific checkers
    fun getBarcodeScanner(): BarcodeScanner = barcodeScanner
    fun getHatDetector(): HatDetector = hatDetector
}