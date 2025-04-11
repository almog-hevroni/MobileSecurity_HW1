package com.example.mobilesecurity_hw1.conditions

 //Interface for all condition checkers
interface ConditionChecker {
     //Initialize the condition checker
    fun initialize()

    //Start checking for the condition
    fun startChecking()

     //Check if the condition is currently met
    fun isConditionMet(): Boolean

     //Update the UI based on condition state
    fun updateUI()

    //Clean up resources
    fun cleanup()

     //Reset the condition state
    fun reset()
}