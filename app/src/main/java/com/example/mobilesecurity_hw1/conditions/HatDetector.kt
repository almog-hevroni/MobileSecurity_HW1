package com.example.mobilesecurity_hw1.conditions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.Locale

// Checker for hat detection condition
class HatDetector(
    activity: Activity,
    smileyView: ImageView
) : BaseConditionChecker(activity, smileyView) {

    // Camera capture launcher
    private var cameraLauncher: ActivityResultLauncher<Intent>? = null

    // Target object to detect
    private val possibleHatLabels = listOf(
        "hat", "cap", "headwear", "headgear", "beanie", "head covering",
        "fedora", "baseball cap", "sun hat", "כובע", "helmet", "bonnet",
        "sombrero", "beret", "headpiece"
    )

    // סף ביטחון נמוך
    private val minimumConfidence = 0.3f // 30% confidence

    private val CAMERA_PERMISSION_CODE = 100

    // Initialize the checker
    override fun initialize() {
        if (activity is AppCompatActivity) {
            // Register for camera result
            cameraLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        analyzeImage(imageBitmap)
                    } else {
                        Toast.makeText(activity, "לא ניתן לקבל את התמונה", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "צילום בוטל", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Start checking
    override fun startChecking() {
        // No continuous checking needed, triggered by user
    }

    // Clean up resources
    override fun cleanup() {
        // No specific cleanup needed
    }

    // Launch camera to take a picture
    fun takePicture() {
        if (activity is AppCompatActivity) {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    // Check if we have camera permission
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    // Open camera after permission is granted
    private fun openCamera() {
        // Create camera intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Check if there's an app that can handle this intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            cameraLauncher?.launch(takePictureIntent)
        } else {
            Toast.makeText(activity, "לא נמצאה אפליקציית מצלמה", Toast.LENGTH_SHORT).show()
        }
    }

    // Analyze the captured image using ML Kit
    private fun analyzeImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                var foundHat = false
                var bestMatchConfidence = 0f
                var bestMatchLabel = ""

                val allLabels = StringBuilder()

                // Check if any label matches possible hat terms
                for (label in labels) {
                    val text = label.text.lowercase(Locale.ROOT)
                    val confidence = label.confidence

                    allLabels.append("${label.text} (${String.format("%.1f", confidence * 100)}%), ")

                    for (hatLabel in possibleHatLabels) {
                        if (text.contains(hatLabel)) {
                            if (confidence > bestMatchConfidence) {
                                bestMatchConfidence = confidence
                                bestMatchLabel = label.text
                            }
                            break
                        }
                    }
                }

                if (bestMatchConfidence >= minimumConfidence) {
                    foundHat = true
                }

                // Update UI based on result
                activity.runOnUiThread {
                    if (foundHat) {
                        isConditionSatisfied = true
                        updateUI()
                        Toast.makeText(
                            activity,
                            "זוהה כובע: $bestMatchLabel (רמת ביטחון: ${String.format("%.1f", bestMatchConfidence * 100)}%)",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            "לא זוהה כובע. נסה שוב. תוויות שזוהו: ${allLabels.toString()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        "שגיאה בזיהוי התמונה: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun reset() {
        if (!isConditionSatisfied) {
            super.reset()
        }
    }
}