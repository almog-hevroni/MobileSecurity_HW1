package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

// Checker for barcode scanning condition
class BarcodeScanner(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView) {

    // Target barcode value that needs to be scanned
    private val targetBarcodeValue = "7290000176420"

    // Barcode scanning launcher using the modern approach
    private var barcodeLauncher: ActivityResultLauncher<ScanOptions>? = null

    // Initialize the checker
    override fun initialize() {
        if (activity is AppCompatActivity) {
            // Register for barcode scanning result using modern approach
            barcodeLauncher = activity.registerForActivityResult(ScanContract()) { result ->
                handleScanResult(result.contents)
            }
        }
    }

    // Start checking
    override fun startChecking() {
        // No continuous checking needed
    }

    // Clean up resources
    override fun cleanup() {
        // No specific cleanup needed
    }

    // Launch the barcode scanner
    fun startScan() {
        if (activity is AppCompatActivity) {
            // Configure scan options with modern approach
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("סרוק את הברקוד של המוצר הספציפי")
                setCameraId(0) // Use back camera
                setBeepEnabled(true)
                setBarcodeImageEnabled(true)
                setOrientationLocked(true) // Allow user to rotate the screen during scanning
            }
            // Launch the scanner activity
            barcodeLauncher?.launch(options)
        }
    }

    // Handle the scan result
    private fun handleScanResult(scannedBarcode: String?) {
        if (scannedBarcode == null) {
            Toast.makeText(activity, "סריקה בוטלה", Toast.LENGTH_SHORT).show()
        } else {
            if (scannedBarcode == targetBarcodeValue) {
                isConditionSatisfied = true
                activity.runOnUiThread {
                    updateUI()
                    Toast.makeText(activity, "נסרק בהצלחה!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "זה לא הברקוד הנכון. נסה שוב.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun reset() {
        if (!isConditionSatisfied) {
            super.reset()
        }
    }
}