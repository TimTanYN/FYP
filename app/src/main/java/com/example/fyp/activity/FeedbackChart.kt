package com.example.fyp.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackChart:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_chart)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupBarChart()
        setupPieChart()
    }



    fun setupBarChart() {
        val barChart = findViewById<BarChart>(R.id.barChart)
        barChart.axisLeft.isEnabled = false

        // Hide the top X-axis
        barChart.xAxis.setDrawAxisLine(true) // This disables drawing the line for the X-axis.
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Ensures the X-axis is at the bottom.

        // Make sure the right Y-axis is enabled
        barChart.axisRight.isEnabled = true

        // Optional: Customize the appearance of the right Y-axis
        barChart.axisRight.setDrawAxisLine(true)
        barChart.axisRight.setDrawGridLines(false)
        // Initialize counters
        var one = 0f
        var two = 0f
        var three = 0f
        var four = 0f
        var five = 0f

        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Feedback")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents.forEach { document ->
                        val appValue = document.getString("appValue")?.toDouble() ?: 0.0
                        val serviceValue = document.getString("serviceValue")?.toDouble() ?: 0.0
                        val performanceValue = document.getString("performanceValue")?.toDouble() ?: 0.0

                        // Increment counters based on rating values
                        arrayOf(appValue, serviceValue, performanceValue).forEach { rating ->
                            when (rating) {
                                1.0 -> one++
                                2.0 -> two++
                                3.0 -> three++
                                4.0 -> four++
                                5.0 -> five++
                                else -> println("Unknown number")
                            }
                        }
                    }

                    // Create a list of BarEntries
                    val entries = ArrayList<BarEntry>().apply {
                        add(BarEntry(1f, one))
                        add(BarEntry(2f, two))
                        add(BarEntry(3f, three))
                        add(BarEntry(4f, four))
                        add(BarEntry(5f, five))
                    }

                    // Update the chart
                    val dataSet = BarDataSet(entries, "Ratings")
                    val data = BarData(dataSet)
                    barChart.data = data
                    barChart.invalidate() // Refresh the chart
                } else {
                    Log.d("Firestore", "No documents found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching documents: ", exception)
            }
    }

    fun setupPieChart() {
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        var one = 0f
        var two = 0f
        var three = 0f
        var four = 0f
        var five = 0f
        var total = 0f  // Change total to a float for consistency

        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Feedback")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents.forEach { document ->
                        // For each document, increment total once
                        total++

                        val appValue = document.getString("appValue")?.toDouble() ?: 0.0
                        val serviceValue = document.getString("serviceValue")?.toDouble() ?: 0.0
                        val performanceValue = document.getString("performanceValue")?.toDouble() ?: 0.0

                        // Increment counters based on rating values
                        arrayOf(appValue, serviceValue, performanceValue).forEach { rating ->
                            when (rating) {
                                1.0 -> one++
                                2.0 -> two++
                                3.0 -> three++
                                4.0 -> four++
                                5.0 -> five++
                                else -> println("Unknown number")
                            }
                        }
                    }

                    // Calculate percentages
                    val onePercent = (one / total) * 100
                    val twoPercent = (two / total) * 100
                    val threePercent = (three / total) * 100
                    val fourPercent = (four / total) * 100
                    val fivePercent = (five / total) * 100

                    // Update PieChart Entries
                    val entries = ArrayList<PieEntry>()
                    if(onePercent > 0) entries.add(PieEntry(onePercent, "Rating 1"))
                    if(twoPercent > 0) entries.add(PieEntry(twoPercent, "Rating 2"))
                    if(threePercent > 0) entries.add(PieEntry(threePercent, "Rating 3"))
                    if(fourPercent > 0) entries.add(PieEntry(fourPercent, "Rating 4"))
                    if(fivePercent > 0) entries.add(PieEntry(fivePercent, "Rating 5"))

                    val dataSet = PieDataSet(entries, "Ratings")
                    dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

                    val data = PieData(dataSet)
                    pieChart.data = data
                    pieChart.invalidate() // Refresh the chart
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching documents: ", exception)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            else -> super.onOptionsItemSelected(item)
        }
    }
}