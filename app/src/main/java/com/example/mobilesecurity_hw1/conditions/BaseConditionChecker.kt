package com.example.mobilesecurity_hw1.conditions

import android.app.Activity
import android.widget.ImageView
import com.example.mobilesecurity_hw1.R

//Base class for all condition checkers
abstract class BaseConditionChecker(
    protected val activity: Activity,
    protected val smileyView: ImageView
) : ConditionChecker {

    // Flag to track condition state
    protected var isConditionSatisfied: Boolean = false

     //Update the UI based on condition state
     override fun updateUI() {
         activity.runOnUiThread {
             if (isConditionSatisfied) {
                 smileyView.setImageResource(R.drawable.happy_face)
             } else {
                 smileyView.setImageResource(R.drawable.sad_face)
             }
         }
     }

    //Check if the condition is currently met
    override fun isConditionMet(): Boolean {
        return isConditionSatisfied
    }

     //Reset the condition state
    override fun reset() {
        isConditionSatisfied = false
        updateUI()
    }
}