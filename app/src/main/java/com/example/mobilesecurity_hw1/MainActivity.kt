package com.example.mobilesecurity_hw1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class MainActivity : AppCompatActivity(), SensorEventListener {

    // UI elements
    private lateinit var btnEnter: Button
    private lateinit var tvHints: TextView
    private lateinit var topLeftMarker: View
    private lateinit var centerMarker: View
    private lateinit var bottomLeftMarker: View
    private lateinit var cardHints: MaterialCardView

    // Touch areas
    private lateinit var touchAreaTopLeft: FrameLayout
    private lateinit var touchAreaCenter: FrameLayout
    private lateinit var touchAreaBottomLeft: FrameLayout

    // Sensors and managers
    private lateinit var sensorManager: SensorManager
    private var compassSensor: Sensor? = null
    private var lightSensor: Sensor? = null

    // Arrays for storing sensor values
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    // Matrices for calculating the direction
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    // Are updates available
    private var accelerometerUpdated = false
    private var magnetometerUpdated = false

    // Condition flags
    private var isTimeWindowValid = false
    private var isPatternTouchValid = false
    private var isUsbCharging = false
    private var isFlashDetected = false
    private var isAirplaneModeOn = false
    private var isCompassRotationValid = false

    // Touch pattern tracking
    private var touchStep = 0

    // Compass rotation tracking
    private var lastAzimuth: Float = -1f // Last angle recorded
    private var rotationCount = 0 // Number of completed rotations
    private var startQuadrant = -1 // Starting quadrant of rotation
    private var currentQuadrant = -1 // Current quadrant
    private var passedQuadrants = BooleanArray(4) // Tracking quadrants passed
    private val requiredRotations = 3 // Number of required rotations (3 rotations)

    // Light threshold for flash detection
    private val flashLightThreshold = 200f
    private var baselineLightLevel = 0f
    private var isLightBaselineSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        btnEnter = findViewById(R.id.btnEnter)
        tvHints = findViewById(R.id.tvHints)
        topLeftMarker = findViewById(R.id.markerTopLeft)
        centerMarker = findViewById(R.id.markerCenter)
        bottomLeftMarker = findViewById(R.id.markerBottomLeft)
        cardHints = findViewById(R.id.cardHints)

        // Initialize touch areas
        touchAreaTopLeft = findViewById(R.id.touchAreaTopLeft)
        touchAreaCenter = findViewById(R.id.touchAreaCenter)
        touchAreaBottomLeft = findViewById(R.id.touchAreaBottomLeft)

        // Initialize sensors
        initSensors()

        // Set up UI interactions
        setupTouchListeners()
        setupEnterButton()

        // Update hints display
        updateHints()

        // Start checking conditions
        startConditionChecks()
    }

    private fun initSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Using the appropriate sensors to calculate direction (accelerometer and magnetometer)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer != null && magnetometer != null) {
            // If we have the two required sensors, we will use them
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            sensorManager.registerListener(
                this,
                magnetometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            compassSensor = accelerometer // Save a link to one of the sensors
        } else {
            // Alternative approach if one of the sensors is missing
            compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            Log.w("Sensors", "המכשיר חסר חיישנים נדרשים לזיהוי מצפן מדויק")
        }

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListeners() {
        // Set up touch listeners for each specific area
        touchAreaTopLeft.setOnClickListener {
            handleTouchAreaClick(0)
        }

        touchAreaCenter.setOnClickListener {
            handleTouchAreaClick(1)
        }

        touchAreaBottomLeft.setOnClickListener {
            handleTouchAreaClick(2)
        }
    }

    private fun setupEnterButton() {
        btnEnter.setOnClickListener {
            checkAllConditions()
        }
    }

    private fun startConditionChecks() {
        // Start time window check
        startTimeWindowCheck()

        // Check USB charging state
        checkChargingState()

        // Check airplane mode
        checkAirplaneMode()
    }

    private fun startTimeWindowCheck() {
        // Check time window condition every second
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                checkTimeWindow()
                handler.postDelayed(this, 1000)
            }
        })
    }

    // Condition 1: Check if current time is within the specific window (first 20 seconds of a minute)
    private fun checkTimeWindow() {
        val calendar = Calendar.getInstance() // Get the current clipboard object
        val seconds = calendar.get(Calendar.SECOND) // Get the current seconds
        isTimeWindowValid = seconds < 20
        updateHints()
    }

    // Condition 2: Handle the touch pattern (top-left, center, bottom-left)
    private fun handleTouchAreaClick(areaIndex: Int) {
        when (touchStep) {
            0 -> {
                // Expected: Top-left touch
                if (areaIndex == 0) {
                    touchStep++
                    topLeftMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        topLeftMarker.visibility = View.INVISIBLE
                    }, 500)
                } else {
                    resetTouchPattern()
                }
            }
            1 -> {
                // Expected: Center touch
                if (areaIndex == 1) {
                    touchStep++
                    centerMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        centerMarker.visibility = View.INVISIBLE
                    }, 500)
                } else {
                    resetTouchPattern()
                }
            }
            2 -> {
                // Expected: Bottom-left touch
                if (areaIndex == 2) {
                    touchStep = 0
                    isPatternTouchValid = true
                    bottomLeftMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        bottomLeftMarker.visibility = View.INVISIBLE
                    }, 500)
                    updateHints()
                } else {
                    resetTouchPattern()
                }
            }
        }
    }

    private fun resetTouchPattern() {
        touchStep = 0
        topLeftMarker.visibility = View.INVISIBLE
        centerMarker.visibility = View.INVISIBLE
        bottomLeftMarker.visibility = View.INVISIBLE
    }

    // Condition 3: Check if device is charging via USB (not wall)
    private fun checkChargingState() {
        val batteryStatus = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED) // Receives a broadcast about a change in battery status
        )

        // Check if the device is connected to a USB charger (code 2) or a wall outlet (code 1)
        val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1 //The EXTRA_PLUGGED value contains information about the charging method: 1 for wall outlet (AC), 2 for USB, 4 for wireless (in newer versions).
        isUsbCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        updateHints()

        // Continue checking charging state
        Handler(Looper.getMainLooper()).postDelayed({
            checkChargingState()
        }, 2000)
    }

    // Condition 4: Check if airplane mode is on
    private fun checkAirplaneMode() {
        isAirplaneModeOn = Settings.Global.getInt(
            contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0 // Checks if airplane mode is on (1) or not (0)
        updateHints()

        // Continue checking airplane mode
        Handler(Looper.getMainLooper()).postDelayed({
            checkAirplaneMode()
        }, 2000)
    }

    // Handle sensor events for light, compass rotation
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                handleLightSensor(event.values[0]) // Handling changes to the light sensor
            }
            Sensor.TYPE_ACCELEROMETER -> {
                // Update accelerometer values
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                accelerometerUpdated = true

                // Try to calculate the direction if we have all the required information
                updateOrientationAngles()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                // Update magnetometer values
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                magnetometerUpdated = true

                // Try to calculate the direction if we have all the required information
                updateOrientationAngles()
            }
        }
    }

    // Function to calculate the direction angle using the data from the sensors
    private fun updateOrientationAngles() {
        // Checking that we have current data from both sensors
        if (!accelerometerUpdated || !magnetometerUpdated) {
            return
        }

        // Calculate the rotation matrix
        val rotationOK = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // If the matrix calculation was successful, calculate the orientation angles
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // Convert to angle in degrees
            val azimuthInRadians = orientationAngles[0]
            val azimuthInDegrees = ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()

            // We pass the value to the function that handles the compass rotations
            handleCompassRotation(azimuthInDegrees)
        }
    }

    // Condition 5: Detect camera flash from another device
    private fun handleLightSensor(lightValue: Float) {
        if (!isLightBaselineSet) {
            baselineLightLevel = lightValue
            isLightBaselineSet = true
        }

        // Detect sudden large increase in light (flash)
        if (lightValue > baselineLightLevel + flashLightThreshold) {
            isFlashDetected = true
            updateHints()
        }
    }

    // Condition 6: Track compass rotations with a quadrant-based approach
    private fun handleCompassRotation(azimuth: Float) {
        // Normalize azimuth to 0-360 range
        val normalizedAzimuth = (azimuth + 360) % 360

        // Get the quadrant (0-3) based on azimuth
        val newQuadrant = (normalizedAzimuth / 90).toInt()

        // If first reading, initialize
        if (lastAzimuth < 0) {
            lastAzimuth = normalizedAzimuth
            startQuadrant = newQuadrant
            currentQuadrant = newQuadrant
            // Reset quadrant tracking
            for (i in passedQuadrants.indices) {
                passedQuadrants[i] = false
            }
            passedQuadrants[newQuadrant] = true // Mark the current quadrant as controlled
            return
        }

        // If quadrant changed
        if (newQuadrant != currentQuadrant) {
            // Mark this quadrant as visited
            passedQuadrants[newQuadrant] = true

            // Check if we've completed a full rotation (visited all 4 quadrants)
            if (passedQuadrants.all { it } && newQuadrant == startQuadrant) {
                // Reset quadrant tracking for next rotation
                for (i in passedQuadrants.indices) {
                    if (i != newQuadrant) {
                        passedQuadrants[i] = false
                    }
                }

                // Increment rotation count
                rotationCount++
                // Check if we've reached the required number of rotations (3)
                if (rotationCount >= requiredRotations) {
                    isCompassRotationValid = true
                    updateHints()
                }
            }

            // Update current quadrant
            currentQuadrant = newQuadrant
        }

        // Update last azimuth
        lastAzimuth = normalizedAzimuth
    }

    // Check if all conditions are met
    private fun checkAllConditions() {
        val allConditionsMet = isTimeWindowValid &&
                isPatternTouchValid &&
                isUsbCharging &&
                isFlashDetected &&
                isAirplaneModeOn &&
                isCompassRotationValid

        if (allConditionsMet) {
            // All conditions met, go to success screen
            val intent = Intent(this, SuccessActivity::class.java)
            startActivity(intent)
        } else {
            // Show which conditions aren't met
            Toast.makeText(this, "לא כל התנאים התקיימו. נסה שוב.", Toast.LENGTH_SHORT).show()
        }
    }

    // Update the hints display
    private fun updateHints() {
        // Check if tvHints has already been initialized to avoid NullPointerException
        if (!::tvHints.isInitialized) return

        val hintBuilder = StringBuilder()

        hintBuilder.append("1. ⏰ ")
        hintBuilder.append("הכניסה אפשרית רק בטווח זמנים מסוים בכל דקה ")
        if (isTimeWindowValid) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }
        hintBuilder.append("<br><br>")  // שימוש ב-<br> במקום \n\n

        hintBuilder.append("2. 👆 ")
        hintBuilder.append("לחץ על האזורים: למעלה שמאל, מרכז, למטה שמאל ברצף כלשהו ")
        if (isPatternTouchValid) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }
        hintBuilder.append("<br><br>")

        hintBuilder.append("3. 🔌 ")
        hintBuilder.append("חבר את המכשיר למטען בחיבור כלשהו ")
        if (isUsbCharging) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }
        hintBuilder.append("<br><br>")

        hintBuilder.append("4. 📸 ")
        hintBuilder.append("נדרש הבזק על המכשיר ")
        if (isFlashDetected) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }
        hintBuilder.append("<br><br>")

        hintBuilder.append("5. ✈️ ")
        hintBuilder.append(" המכשיר נדרש להיות במצב כלשהו ")
        if (isAirplaneModeOn) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }
        hintBuilder.append("<br><br>")

        hintBuilder.append("6. 🧭 ")
        hintBuilder.append("נדרש לסובב את המכשיר כמות כלשהי של פעמים ")
        if (isCompassRotationValid) {
            hintBuilder.append("<font color='#4CAF50'>✓</font>")
        } else {
            hintBuilder.append("<font color='#F44336'>✗</font>")
        }

        try {
            // Enable basic HTML support in the text field, with error handling
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvHints.text = Html.fromHtml(hintBuilder.toString(), Html.FROM_HTML_MODE_COMPACT)
            } else {
                // Older versions of Android
                @Suppress("DEPRECATION")
                tvHints.text = Html.fromHtml(hintBuilder.toString())
            }
        } catch (e: Exception) {
            // In case of an error, we will display plain text without formatting
            Log.e("MainActivity", "Error setting HTML text: ${e.message}")
            tvHints.text = hintBuilder.toString()
                .replace("<br><br>", "\n\n")
                .replace("<font color='#4CAF50'>✓</font>", "✓")
                .replace("<font color='#F44336'>✗</font>", "✗")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onResume() {
        super.onResume()

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        magnetometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        lightSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        isLightBaselineSet = false // Reset the lighting baseline
        checkAirplaneMode()
        checkChargingState()

        // Reset compass rotation tracking
        lastAzimuth = -1f
        rotationCount = 0
        startQuadrant = -1
        currentQuadrant = -1
        for (i in passedQuadrants.indices) {
            passedQuadrants[i] = false
        }

        // Reset sensor initialization variables
        accelerometerUpdated = false
        magnetometerUpdated = false
    }

    override fun onPause() {
        super.onPause()
        // Unregister all sensors
        sensorManager.unregisterListener(this)
    }
}
