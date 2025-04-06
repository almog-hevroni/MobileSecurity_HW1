package com.example.mobilesecurity_hw1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {

    private lateinit var tvConditionsSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        // Initialize UI elements
        tvConditionsSummary = findViewById(R.id.tvConditionsSummary)

        // Display summary of conditions
        updateConditionsSummary()
    }

    private fun updateConditionsSummary() {
        val summaryBuilder = StringBuilder()

        summaryBuilder.append("כל התנאים הבאים התקיימו:<br><br>")

        summaryBuilder.append("1. ⏰ ")
        summaryBuilder.append("הכניסה בוצעה ב-20 השניות הראשונות של הדקה ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font><br><br>")

        summaryBuilder.append("2. 👆 ")
        summaryBuilder.append("תבנית המגע הנכונה בוצעה - למעלה שמאל, מרכז, למטה שמאל ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font><br><br>")

        summaryBuilder.append("3. 🔌 ")
        summaryBuilder.append("המכשיר חובר למטען USB ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font><br><br>")

        summaryBuilder.append("4. 📸 ")
        summaryBuilder.append("זוהה הבזק מצלמה ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font><br><br>")

        summaryBuilder.append("5. ✈️ ")
        summaryBuilder.append("מצב טיסה הופעל ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font><br><br>")

        summaryBuilder.append("6. 🧭 ")
        summaryBuilder.append("בוצעו שלושת הסיבובים הנדרשים במצפן ")
        summaryBuilder.append("<font color='#4CAF50'>✓</font>")

        tvConditionsSummary.text = android.text.Html.fromHtml(summaryBuilder.toString(), android.text.Html.FROM_HTML_MODE_COMPACT)
    }
}