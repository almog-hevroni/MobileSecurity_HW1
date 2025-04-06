# Advanced Authentication System

## Overview

This Android application demonstrates an advanced multi-factor authentication system using various device sensors and conditions. Users must meet all six authentication conditions to gain access to the system. The application is written in Kotlin and designed with a focus on mobile security.

## Authentication Conditions

The app requires users to fulfill all of the following conditions for successful authentication:

1. **⏰ Time Window Authentication**
    - Access is restricted to the first 20 seconds of any minute
    - Requires precise timing for authentication attempts

2. **👆 Specific Touch Pattern**
    - Users must tap three areas in the correct sequence:
        - Top-left area of the screen
        - Center of the screen
        - Bottom-left area of the screen
    - Visual markers appear briefly to indicate successful touches

3. **🔌 USB Charging Detection**
    - The device must be connected to a USB charger (not a wall charger)
    - Uses battery management APIs to detect the charging method

4. **📸 Camera Flash Detection**
    - Requires a camera flash from another device to be detected
    - Uses the light sensor to detect sudden changes in ambient light

5. **✈️ Airplane Mode Required**
    - The device must be in Airplane Mode
    - Uses system settings to check the airplane mode status

6. **🧭 Compass Rotation Authentication**
    - The user must rotate the device completely 3 times
    - Uses accelerometer and magnetometer sensors to track device rotation

## Sensor Usage

The application leverages various Android sensors and system APIs:

### 1. System Clock
- Uses `Calendar.getInstance()` to check the current time for the time window authentication
- Continuously monitors time using a Handler with postDelayed for regular checks

### 2. Touch Sensors
- Implements custom touch areas with `FrameLayout` components
- Uses click listeners to detect touches in specific regions of the screen
- Visual feedback through circular markers that appear briefly when touched

### 3. Battery Management API
- Uses `BatteryManager` and `Intent.ACTION_BATTERY_CHANGED` broadcast receiver
- Specifically checks for `BatteryManager.BATTERY_PLUGGED_USB` to verify USB charging

### 4. Light Sensor
- Utilizes `Sensor.TYPE_LIGHT` to detect ambient light levels
- Establishes a baseline light level and then detects significant increases (camera flash)
- Implementation note: Requires `android.hardware.sensor.light` feature in manifest

### 5. System Settings
- Reads `Settings.Global.AIRPLANE_MODE_ON` to check if Airplane Mode is enabled
- Continuously polls this setting to detect changes

### 6. Compass/Orientation Sensors
- Uses a combination of:
    - `Sensor.TYPE_ACCELEROMETER`: Detects device acceleration forces
    - `Sensor.TYPE_MAGNETIC_FIELD`: Detects Earth's magnetic field
- Combines data to calculate orientation angles using:
    - `SensorManager.getRotationMatrix()`
    - `SensorManager.getOrientation()`
- Tracks device rotation through quadrants to count complete 360° rotations

## Technical Implementation

### Sensor Registration
```kotlin
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
    
    // Reset sensors and states
    // ...
}