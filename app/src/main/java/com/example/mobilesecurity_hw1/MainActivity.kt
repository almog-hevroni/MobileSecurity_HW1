package com.example.mobilesecurity_hw1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilesecurity_hw1.conditions.ConditionManager

//Main activity with refactored condition logic using separate checker classes
class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var btnEnter: Button
    private lateinit var tvTitle: TextView

    // Smiley faces
    private lateinit var smiley1: ImageView // Time window
    private lateinit var smiley2: ImageView // Touch pattern
    private lateinit var smiley3: ImageView // USB charging
    private lateinit var smiley4: ImageView // Flash detection
    private lateinit var smiley5: ImageView // Airplane mode
    private lateinit var smiley6: ImageView // Barcode scanning
    private lateinit var smiley7: ImageView // Hat detection

    // Touch areas
    private lateinit var touchAreaTopLeft: FrameLayout
    private lateinit var touchAreaCenter: FrameLayout
    private lateinit var touchAreaBottomLeft: FrameLayout

    // Condition manager
    private lateinit var conditionManager: ConditionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        initializeUI()

        // Set up condition manager
        setupConditionManager()

        // Set up buttons
        setupButtons()

        // Set up click listener for clickable smileys
        setupSmileyClickListeners()
    }

    //Initialize UI elements
    private fun initializeUI() {
        // Main UI elements
        btnEnter = findViewById(R.id.btnEnter)
        tvTitle = findViewById(R.id.tvTitle)

        // Smiley faces
        smiley1 = findViewById(R.id.smiley1)
        smiley2 = findViewById(R.id.smiley2)
        smiley3 = findViewById(R.id.smiley3)
        smiley4 = findViewById(R.id.smiley4)
        smiley5 = findViewById(R.id.smiley5)
        smiley6 = findViewById(R.id.smiley6)
        smiley7 = findViewById(R.id.smiley7)

        // Touch areas
        touchAreaTopLeft = findViewById(R.id.touchAreaTopLeft)
        touchAreaCenter = findViewById(R.id.touchAreaCenter)
        touchAreaBottomLeft = findViewById(R.id.touchAreaBottomLeft)
    }

    //Set up condition manager
    private fun setupConditionManager() {
        conditionManager = ConditionManager(this)
        conditionManager.initialize(
            smiley1, smiley2, smiley3, smiley4, smiley5, smiley6, smiley7,
            touchAreaTopLeft, touchAreaCenter, touchAreaBottomLeft
        )
        conditionManager.startChecking()
    }

    // Set up smiley click listeners
    private fun setupSmileyClickListeners() {
        // Click listener for barcode scanning smiley
        smiley6.setOnClickListener {
            conditionManager.getBarcodeScanner().startScan()
        }

        // Click listener for hat detection smiley
        smiley7.setOnClickListener {
            conditionManager.getHatDetector().takePicture()
        }
    }

    //Set up buttons
    private fun setupButtons() {
        // Set up enter button
        btnEnter.setOnClickListener {
            checkAllConditions()
        }
    }

    //Check if all conditions are met and navigate to success screen if true
    private fun checkAllConditions() {
        if (conditionManager.areAllConditionsMet()) {
            // All conditions met, go to success screen
            val intent = Intent(this, SuccessActivity::class.java)
            startActivity(intent)
        } else {
            // Show which conditions aren't met
            Toast.makeText(this, "לא כל התנאים התקיימו. נסה שוב.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume condition checks
        conditionManager.resume()
    }

    override fun onPause() {
        super.onPause()
        // Clean up resources
        conditionManager.cleanup()
    }

    override fun onDestroy() {
        super.onDestroy()
        conditionManager.cleanup()
    }
}