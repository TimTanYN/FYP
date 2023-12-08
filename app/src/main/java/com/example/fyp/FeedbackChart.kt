package com.example.fyp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.adapter.Feedback
import com.example.fyp.adapter.FeedbackAdapter
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


        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40.0f, "Category 1"))
        entries.add(PieEntry(30.0f, "Category 2"))
        entries.add(PieEntry(20.0f, "Category 3"))
        entries.add(PieEntry(10.0f, "Category 4"))

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // Refresh the chart
    }


}