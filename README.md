# 📱 Advanced Authentication System

## Overview

This Android application demonstrates an advanced multi-factor authentication system using various device sensors and conditions. Users must meet all six authentication conditions to gain access to the system. The application is written in Kotlin and designed with a focus on mobile security and implementing different sensor-based authentication factors.

## 🔐 Authentication Conditions

The app requires users to fulfill all of the following conditions for successful authentication:

1. **⏰ Time Window Authentication**
    - Access is restricted to the first 20 seconds of any minute
    - Continuously monitors time to check this condition

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

6. **📱 Barcode Scanning Authentication**
    - The user must scan a specific barcode (7290000176420)
    - Uses a barcode scanning library to validate the correct product
7. **🧢 Hat Detection**
   - The user must take a photo of a hat
   - Uses ML Kit image labeling to identify hats in photos

## 🏗️ Application Structure

The application follows a clean, modular architecture:

### Core Components

- **MainActivity**: Entry point that manages the UI and condition checking
- **SuccessActivity**: Displays success message when all conditions are met
- **ConditionManager**: Coordinates all condition checkers

### Condition Checkers

The app uses a pattern of individual condition checker classes that all implement the `ConditionChecker` interface:

1. **BaseConditionChecker**: Abstract class implementing common functionality
    - Manages UI updates for condition state
    - Handles basic condition state tracking

2. **Specialized Condition Checkers**:
    - **TimeWindowChecker**: Checks if current time is within first 20 seconds
    - **TouchPatternChecker**: Manages the three-point touch pattern sequence
    - **UsbChargingChecker**: Verifies USB charging status
    - **FlashDetector**: Uses light sensor to detect camera flash
    - **AirplaneModeChecker**: Monitors airplane mode status
    - **BarcodeScanner**: Handles barcode scanning and validation
    - **HatDetector**: Uses ML Kit to detect a hat in a photo.

## Sensor and API Usage

The application leverages various Android sensors and system APIs:

### 1. System Clock
- Uses `Calendar.getInstance()` to check the current time
- Continuously monitors time using a Handler with postDelayed

### 2. Touch System
- Implements custom touch areas with `FrameLayout` components
- Uses click listeners to detect touches in specific regions
- Provides visual feedback through custom circular markers

### 3. Battery Management API
- Uses `BatteryManager` and `Intent.ACTION_BATTERY_CHANGED`
- Specifically checks for `BatteryManager.BATTERY_PLUGGED_USB`

### 4. Light Sensor
- Utilizes `Sensor.TYPE_LIGHT` to detect ambient light levels
- Establishes a baseline and detects significant increases (flash)
- Implements `SensorEventListener` for real-time monitoring

### 5. System Settings
- Reads `Settings.Global.AIRPLANE_MODE_ON` to check airplane mode
- Continuously polls this setting to detect changes

### 6. Barcode Scanning
- Uses the ZXing library (via journeyapps barcode-scanner)
- Implements `ActivityResultLauncher` for modern barcode scanning
- Validates against a specific target barcode

### 7. Image Recognition
- Uses Google ML Kit's image labeling API
- Captures photos using Android's camera intent
- Analyzes images to detect hats with confidence threshold of 30%

## Setting Up the Project

1. Clone the repository
    ```sh 
        git clone https://github.com/almog-hevroni/MobileSecurity_HW1
    ```
2. Open the project in Android Studio
3. Add the required dependencies in your build.gradle:
   ```gradle
   implementation(libs.zxing.android.embedded)
   implementation (libs.image.labeling)

4. Build and run on a device with the required sensors (light sensor, etc.)

## 🔍 Testing the Application

To successfully authenticate:
1. Open the app within the first 20 seconds of any minute
2. Complete the touch pattern (top-left → center → bottom-left)
3. Connect your device to a USB charger
4. Have someone flash a camera light at your device
5. Enable Airplane Mode
6. Scan the required barcode (7290000176420)
7. Press the "כניסה למערכת" (Enter System) button

## 🧩 Extending the Project

The application is designed in a modular structure making it easy to add new authentication conditions:

1. Create a new class extending BaseConditionChecker
2. Implement the required interface methods
3. Add the new class to the ConditionManager