package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.mobilesecurity_hw1.R

//Checker for touch pattern condition (top-left, center, bottom-left)
class TouchPatternChecker(
    activity: Activity,
    smileyView: ImageView,
    private val touchAreaTopLeft: FrameLayout,
    private val touchAreaCenter: FrameLayout,
    private val touchAreaBottomLeft: FrameLayout
) : BaseConditionChecker(activity, smileyView) {

    // Touch pattern tracking
    private var touchStep = 0

    // Visual markers for feedback
    private lateinit var topLeftMarker: View
    private lateinit var centerMarker: View
    private lateinit var bottomLeftMarker: View

    //Initialize markers and touch listeners
    override fun initialize() {
        addTouchMarkers()
        setupTouchListeners()
    }

    //Start checking
    override fun startChecking() {
        // Pattern checking is event-driven, no continuous checking needed
    }

    //Clean up resources
    override fun cleanup() {
        // No specific cleanup needed
    }

    //Reset the touch pattern
    override fun reset() {
        super.reset()
        touchStep = 0
        if (::topLeftMarker.isInitialized && ::centerMarker.isInitialized && ::bottomLeftMarker.isInitialized) {
            topLeftMarker.visibility = View.INVISIBLE
            centerMarker.visibility = View.INVISIBLE
            bottomLeftMarker.visibility = View.INVISIBLE
        }
    }

    //Add visual markers for touch feedback
    private fun addTouchMarkers() {
        // Define size directly (24dp)
        val markerSizePx = (24 * activity.resources.displayMetrics.density).toInt()

        // Add top-left marker
        topLeftMarker = View(activity)
        topLeftMarker.layoutParams = FrameLayout.LayoutParams(
            markerSizePx,
            markerSizePx
        ).apply {
            gravity = android.view.Gravity.CENTER
        }
        topLeftMarker.background = activity.getDrawable(R.drawable.circle_marker)
        topLeftMarker.visibility = View.INVISIBLE
        touchAreaTopLeft.addView(topLeftMarker)

        // Add center marker
        centerMarker = View(activity)
        centerMarker.layoutParams = FrameLayout.LayoutParams(
            markerSizePx,
            markerSizePx
        ).apply {
            gravity = android.view.Gravity.CENTER
        }
        centerMarker.background = activity.getDrawable(R.drawable.circle_marker)
        centerMarker.visibility = View.INVISIBLE
        touchAreaCenter.addView(centerMarker)

        // Add bottom-left marker
        bottomLeftMarker = View(activity)
        bottomLeftMarker.layoutParams = FrameLayout.LayoutParams(
            markerSizePx,
            markerSizePx
        ).apply {
            gravity = android.view.Gravity.CENTER
        }
        bottomLeftMarker.background = activity.getDrawable(R.drawable.circle_marker)
        bottomLeftMarker.visibility = View.INVISIBLE
        touchAreaBottomLeft.addView(bottomLeftMarker)
    }

    //Set up touch listeners for each specific area
    private fun setupTouchListeners() {
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

    //Handle touch events on specific areas
    private fun handleTouchAreaClick(areaIndex: Int) {
        when (touchStep) {
            0 -> {
                // Expected: Top-left touch
                if (areaIndex == 0) {
                    touchStep++
                    // Show visual feedback
                    topLeftMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        topLeftMarker.visibility = View.INVISIBLE
                    }, 500)
                } else {
                    reset()
                }
            }
            1 -> {
                // Expected: Center touch
                if (areaIndex == 1) {
                    touchStep++
                    // Show visual feedback
                    centerMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        centerMarker.visibility = View.INVISIBLE
                    }, 500)
                } else {
                    reset()
                }
            }
            2 -> {
                // Expected: Bottom-left touch
                if (areaIndex == 2) {
                    touchStep = 0
                    isConditionSatisfied = true
                    // Show visual feedback
                    bottomLeftMarker.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        bottomLeftMarker.visibility = View.INVISIBLE
                    }, 500)
                    updateUI()
                } else {
                    reset()
                }
            }
        }
    }
}